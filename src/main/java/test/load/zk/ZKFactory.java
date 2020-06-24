package test.load.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : ZKFactory
 * @Description :
 * @Author :
 * @Date: 2020-06-09 18:55
 */
public class ZKFactory implements Watcher {

    private static final Logger log = LoggerFactory.getLogger(ZKFactory.class);

    private static final String ZK_Host = "127.0.0.1:2181";

    private ZooKeeper zooKeeper;

    private Map<String, String> ZKData;

    private static final Map<String, NotifyCallback> callbackMap = new HashMap<String, NotifyCallback>();

    private static final Object lock = new Object();

    private static class LazyHolder {
        private static final ZKFactory INSTANCE = new ZKFactory();
    }

    public static ZKFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    public ZKFactory() {
        init();
    }

    private void init() {
        int sessionTimeout = (int) TimeUnit.MINUTES.toMillis(1);
        synchronized (this) {
            try {
                if (zooKeeper != null) {
                    zooKeeper.close();
                }

                log.info("建立zooKeeper连接...");
                zooKeeper = new ZooKeeper(ZK_Host, sessionTimeout, this);
                List<String> children = zooKeeper.getChildren(ZKConfigKey.filterServerPath, true);
                for (String cd : children) {
                    log.info("children:{}", cd);
                }

                reload();
            } catch (InterruptedException | KeeperException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reload() throws InterruptedException, KeeperException {
        synchronized (this) {
            Map<String, String> newConfigData = new HashMap<String, String>();
            List<String> children = zooKeeper.getChildren(ZKConfigKey.filterServerPath, true);
            log.info("children--size {}", children.size());

            for (String child : children) {
                log.debug("child of {}: {}", ZKConfigKey.filterServerPath, child);
                Stat stat = new Stat();
                byte[] data = zooKeeper.getData(ZKConfigKey.filterServerPath + "/" + child, true, stat);
                newConfigData.put(child, data == null ? null : new String(data));
            }

            this.ZKData = newConfigData;
        }
    }

    public static Map<String, String> getAllNode(String rootPath) {
        if (StringUtils.isBlank(rootPath)) {
            return Collections.emptyMap();
        }
        ZooKeeper zooKeeper = getInstance().zooKeeper;
        Map<String, String> map = null;
        try {
            List<String> children = zooKeeper.getChildren(rootPath, true);
            Stat stat = new Stat();
            map = new HashMap<>();
            for (String path : children) {
                if (StringUtils.isBlank(path)) {
                    break;
                }
                String wholePath = rootPath + "/" + path;
                String[] split = StringUtils.split(path, "-");
                byte[] data = zooKeeper.getData(wholePath, true, stat);
                if (data != null) {
                    map.put(split[0], new String(data));
                }
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void registerHashRingNode(String url, String data) {
        try {
            String wholePath = ZKConfigKey.filterServerPath + "/" + url + ZKConfigKey.filterServerNode;
            if (getInstance().zooKeeper.exists(wholePath, false) == null) {
                log.debug("notify by create path: wholePath={}, data={}", wholePath, data);
                getInstance().zooKeeper.create(wholePath, data.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } else {
                log.debug("node is exists !!! wholePath={}, data={}", wholePath, data);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("监听事件  ------  WatchedEvent:{}", event.toString());
        boolean reload = false;

        switch (event.getState()) {
            case Expired:
                // 过期了，需要重连
                init();
                break;

            default:
                break;
        }

        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    // client-server 重新连接
                }
                break;
            case NodeCreated:
            case NodeChildrenChanged:
            case NodeDeleted:
            case NodeDataChanged:

                reload = true;
                break;

            default:
                log.debug("ignore event({}): {}, {}", ZKConfigKey.filterServerPath, event.getType(), event);
                break;
        }


        if (reload) {
            try {
                log.info("reload notify config for event({}): {}, {}", ZKConfigKey.filterServerPath,
                        event.getType(), event);
                Map<String, String> oldMap = ZKData;
                if (oldMap == null) {
                    // 有可能未初始化
                    oldMap = Collections.emptyMap();
                }
                // 获取新值
                reload();

                // 比较变更
                for (Map.Entry<String, String> entry : ZKData.entrySet()) {
                    String path = "/" + entry.getKey();
                    String path1 = entry.getKey();
                    String oldData = oldMap.get(path1);
                    String newData = entry.getValue();
                    System.out.println("变更通知");
                    System.out.println("path:" + path1);

                    if (!StringUtils.equals(oldData, newData)) {
                        log.info("改变 path:{} {}--->{}", path, oldData, newData);
                        // raw数据里有时间戳，不一致就通知
                        NotifyCallback cb = callbackMap.get(path);
                        if (null == cb) {
                            callbackMap.entrySet().stream()
                                    .filter(e -> e.getKey().contains(ZKConfigKey.filterServerNode))
                                    .forEach(e -> {
                                        NotifyCallback callback = e.getValue();
                                        log.info("通知 {} 进行重新计算hash环 ", e.getKey());
                                        callback.onChange(path, oldData, newData);
                                    });
                            break;
                        }
                        log.info("通知变更: callback={}, path={}, data={} -> {}",
                                cb.getClass().getName(), path, oldData, newData);
                        try {
                            cb.onChange(path, oldData, newData);
                        } catch (Throwable t) {
                            log.warn("通知处理失败", t);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("事件处理失败", e);
            }
        }

    }

    public static void registerCallback(String path, NotifyCallback notifyCallback) {
        synchronized (lock) {
            log.info("注册通知回调: {}, {}", path, notifyCallback);
            callbackMap.put(path, notifyCallback);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (zooKeeper != null) {
            zooKeeper.close();
        }
        super.finalize();
    }


}
