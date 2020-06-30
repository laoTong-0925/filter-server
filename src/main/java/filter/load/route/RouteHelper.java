package filter.load.route;

import filter.load.hash.CRCHashStrategy;
import filter.load.hash.HashStrategy;
import filter.load.model.ServerNode;
import filter.load.zk.ConfigStringListKeys;
import filter.load.zk.ZKConfigKey;
import filter.load.zk.ZKFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName : RouteHelper
 * @Description : 路由服务,通过哈希从一致性哈希环上获取节点
 * @Author : t_t
 * @Date: 2020-06-30 13:19
 */
public class RouteHelper {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RouteHelper.class);

    private static final HashStrategy hashStrategy = new CRCHashStrategy();

    private static int VIRTUAL_NODE_SIZE = ZKConfigKey.VIRTUAL_NODE_SIZE;

    /**
     * 哈希环,响应callBack
     */
    private static List<ServerNode> sortedHashRing;

    static {
        ZKFactory.registerCallback(ConfigStringListKeys.ThriftMatchFilterServer.name(),
                (path, oldData, newData) -> {
                    if (StringUtils.isNotBlank(path) || StringUtils.isNotBlank(newData)) {
                        String filterName = ConfigStringListKeys.ThriftMatchFilterServer.name();
                        if (path.contains(filterName)) {
                            Map<String, String> realNodeMap = ZKFactory.getAllNode(ZKConfigKey.filterServerPath);
                            if (realNodeMap == null) {
                                logger.warn("ZK获取不到过滤服务！！！");
                                return;
                            }
                            logger.info("从ZK获取过滤服务节点为:{}", realNodeMap);
                            Map<Integer, String> temporaryHashRing = new HashMap<>();
                            realNodeMap.forEach((key, value) -> buildHashRing(key, value, temporaryHashRing));
                            //排序造环
                            sortedHashRing = temporaryHashRing.entrySet().stream()
                                    .map(e -> new ServerNode(e.getKey(), e.getValue()))
                                    .sorted(Comparator.comparing(ServerNode::getHash))
                                    .collect(Collectors.toList());
                        }
                    } else {
                        logger.warn("新过滤服务路径为空或Data为空 path:{} newData:{}", path, newData);
                    }
                });
    }

    public static List<ServerNode> getSortedHashRing() {
        return sortedHashRing;
    }

    /**
     * 路由
     *
     * @param key            用户Id
     * @param sortedHashRing 哈希环
     * @return ServerNode 访问的节点
     */
    public static ServerNode routeServer(int key, List<ServerNode> sortedHashRing) {
        if (key == 0 || CollectionUtils.isEmpty(sortedHashRing))
            return null;
        int size = sortedHashRing.size();
        for (int i = 0; i < size; i++) {
            ServerNode head = sortedHashRing.get(0);
            ServerNode tail = sortedHashRing.get(size - 1);
            if (head != null && tail != null && (key <= head.getHash() || key > tail.getHash())) {//边界处理
                return head;
            } else if (key <= sortedHashRing.get(i).getHash() && key > sortedHashRing.get(i - 1).getHash()) {
                return sortedHashRing.get(i);
            }
        }
        return null;
    }

    /**
     * 构建哈希环
     *
     * @param url  ip:port
     * @param data ip:port
     */
    private static void buildHashRing(String url, String data, Map<Integer, String> temporaryHashRing) {
        for (int i = 0; i <= 100; i++) {
            int serverHashCode = hashStrategy.getHashCode(url + i);
            if (!temporaryHashRing.containsKey(serverHashCode)) {//冲突了继续
                temporaryHashRing.put(serverHashCode, data);
            } else {
                continue;
            }
            if (VIRTUAL_NODE_SIZE <= 0) {
                VIRTUAL_NODE_SIZE = ZKConfigKey.VIRTUAL_NODE_SIZE;
                return;
            }
            VIRTUAL_NODE_SIZE--;
        }
    }

}