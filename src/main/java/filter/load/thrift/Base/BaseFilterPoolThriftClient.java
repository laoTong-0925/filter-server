package filter.load.thrift.Base;

import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import com.wealoha.thrift.ThriftClientPool;
import filter.load.zk.ConfigStringListKeys;
import filter.load.zk.ZKConfigKey;
import filter.load.zk.ZKFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : BasePoolThriftClient
 * @Description : filter-thrift 连接池
 * @Author : t_t
 * @Date: 2020-06-29 13:19
 */
public abstract class BaseFilterPoolThriftClient<T extends TServiceClient> {

    protected static Logger logger = LoggerFactory.getLogger(BaseFilterPoolThriftClient.class);

    private Map<String, ThriftClientPool<T>> clientPoolMap;

    private List<String> serviceList;

    // host:port
    private Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");

    protected void reloadClientPoolMap() {
        logger.info("收到通知准备reload配置数据...");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            logger.error("error:", e);
        }
        List<String> oldServerList = serviceList;
        List<ServiceInfo> servicesList = getServicesList();
        if (CollectionUtils.isEmpty(servicesList)) {
            logger.warn("无可用服务");
            return;
        }
        PoolConfig config = getConfig();
        logger.info("变更服务: old:{} ---> new:{}", oldServerList, servicesList);
        //服务只会增加 servicesList > oldServerList
        if (!CollectionUtils.isEmpty(oldServerList)) {
            servicesList.removeAll(oldServerList);
        }
        servicesList.forEach(e -> clientPoolMap.put(getUrl(e), new ThriftClientPool<>(Collections.singletonList(e), this::getClient, config)));
    }

    /**
     * 获取配置服务的ip和host信息 <br/>
     * eg: <code>
     * <pre>
     *     protected ConfigStringListKeys getConfigKey() {
     *         return ConfigStringListKeys.ThriftImageUploadServer;
     *     }
     * </pre>
     * </code>
     *
     * @return ConfigStringListKeys的枚举
     */
    protected abstract ConfigStringListKeys getConfigKey();

    /**
     * 调整池的配置，默认:<br/>
     * 100个最大连接，2个静默连接
     *
     * @param poolConfig PoolConfig
     */
    protected void adjustPoolConfig(PoolConfig poolConfig) {
    }

    /**
     * 获取与thrift的底层连接client
     * eg:<code>
     * <pre>
     *     public TServiceClient makeClient(TTransport transport) {
     *         return new Client(new TBinaryProtocol(transport));
     *     }
     * </pre>
     * </code>
     *
     * @param transport
     * @return
     */
    protected abstract T getClient(TTransport transport);

    protected ThriftClientPool<T> getClientPool(ServiceInfo service) {
        if (service == null)
            return null;
        if (clientPoolMap == null) {
            synchronized (this) {
                if (clientPoolMap == null) {
                    clientPoolMap = new HashMap<>();
                    logger.info("服务正在初始化...");
                    List<ServiceInfo> services = getServicesList();
                    if (services.size() == 0) {
                        throw new RuntimeException("服务初始化失败,未找到任何可用服务");
                    }
                    PoolConfig config = getConfig();
                    services.forEach(e -> clientPoolMap.put(getUrl(e), new ThriftClientPool<>(Collections.singletonList(e), this::getClient, config)));
                    logger.info("服务初始化完成");
                }
            }
        }
        logger.debug("服务已存在，直接返回");
        return clientPoolMap.get(getUrl(service));
    }

    private PoolConfig getConfig() {
        PoolConfig config = new PoolConfig();
        config.setMinIdle(2);
        config.setMaxTotal(100);
        config.setTimeout(5000);
        logger.info("设置 TimeOut:5000");
        config.isFailover();
        logger.info("设置 isFailOver: true");
        adjustPoolConfig(config);
        return config;
    }

    protected String getUrl(ServiceInfo service) {
        return service.getHost() + ":" + service.getPort();
    }

    protected List<ServiceInfo> getServicesList() {
        Map<String, String> allNode = ZKFactory.getAllNode(ZKConfigKey.filterServerPath);
        List<String> list = new ArrayList<>(allNode.values());
//        List<String> list = Config.instance.get(getConfigKey());
        List<ServiceInfo> services = new ArrayList<>();
        for (String item : list) {
            Matcher matcher = p.matcher(item);
            if (matcher.find()) {
                String host = matcher.group(1);
                String port = matcher.group(2);
                services.add(new ServiceInfo(host, Integer.parseInt(port)));
                logger.info("添加一个服务： {}:{}", host, port);
            }
        }
        serviceList = list;
        return services;
    }

}
