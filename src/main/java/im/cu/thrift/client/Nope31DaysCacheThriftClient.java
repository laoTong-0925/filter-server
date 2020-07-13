package im.cu.thrift.client;

import com.wealoha.common.config.Config;
import com.wealoha.common.notify.NotifyByZookeeper;
import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import im.cu.base.constants.ConfigStringListKeys;
import im.cu.helper.hashRing.HashRingHelper;
import im.cu.model.ServerNode;
import im.cu.route.RouteHelper;
import im.cu.service.impl.RangeService;
import im.cu.thrift.base.BaseFilterPoolThriftClient;
import im.cu.thrift.service.gen.Nope31DaysCacheThriftService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : FilterServerClient
 * @Description : 客户端
 * @Author : t_t
 * @Date: 2020-06-28 12:38
 */
@Service
public class Nope31DaysCacheThriftClient extends BaseFilterPoolThriftClient<Nope31DaysCacheThriftService.Client> {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Nope31DaysCacheThriftClient.class);

    /**
     * 哈希环,响应callBack
     */
    private List<ServerNode> sortedHashRing;

    @Autowired
    private RangeService rangeService;

    @PostConstruct
    private void init() {
        List<String> list = Config.instance.get(getConfigKey());
        loadHashRing(list);
        register();
    }

    /**
     * 重载哈希环\连接池\BItMap
     */
    private void register() {
        NotifyByZookeeper.registerCallback(getConfigKey().configKey(),
                (oldData, newData) -> {
                    logger.info("回调 重载哈希环\\连接池\\BItMap");
                    //处理新的url数据为List
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {}
                    List<String> list = Config.instance.get(getConfigKey());
                    loadHashRing(list);
                    reloadClientPoolMap();
                    rangeService.loadHashRange(true);
                });
    }

    private void loadHashRing(List<String> realNodeList) {
        logger.info("client 开始加载哈希环  构建RPC连接池");
        if (CollectionUtils.isEmpty(realNodeList)) {
            logger.warn("ZK获取不到过滤服务！！！");
            return;
        }
        logger.info("从ZK获取过滤服务节点为:{}", realNodeList);
        List<Integer> thisServerForHashRing = new ArrayList<>();
        sortedHashRing = HashRingHelper.reloadHashRing(realNodeList, thisServerForHashRing);
        logger.info("---------------客户端加载新Hash环---------------");
        logger.info("------ | 0 ");
        sortedHashRing.forEach(e -> logger.info("------ | {}   url:{}", e.getHash(), e.getUrl()));
        logger.info("------ | I.Max ");
    }

    /**
     * 获取用户过滤结果,查找存在
     *
     * @param userId  目标用户
     * @param userIds 物料
     * @return Set<Integer> 过滤后的用户Id
     */
    public Set<Integer> findExists(int userId, List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds))
            return null;
        ServerNode serverNode = RouteHelper.routeServer(userId, sortedHashRing);
        if (serverNode == null)
            return null;
        System.out.println("客户端获得调用节点 " + serverNode.getUrl());
        String[] split = StringUtils.split(serverNode.getUrl(), ":");
        if (split.length != 2 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])) {
            logger.warn("user {} 获取到的服务存在问题 severNode:{}", userId, serverNode);
            return null;
        }
        Nope31DaysCacheThriftService.Iface client = getClientPool(new ServiceInfo(split[0], Integer.parseInt(split[1]))).iface();
        if (client == null) {
            logger.warn("获取到空的client userId:{}", userId);
            return null;
        }
        try {
            logger.info("-----发出请求----");
            return client.findExists(userId, userIds);
        } catch (TException e) {
            logger.error("FilterServerClient.findNotExit() 调用失败 error", e);
        }
        return null;
    }


    @Override
    protected ConfigStringListKeys getConfigKey() {
        return ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2;
    }

    @Override
    protected Nope31DaysCacheThriftService.Client getClient(TTransport transport) {
        return new Nope31DaysCacheThriftService.Client(new TBinaryProtocol(new TFramedTransport(transport)));
    }

    @Override
    protected void adjustPoolConfig(PoolConfig poolConfig) {
        poolConfig.setTimeout((int) TimeUnit.SECONDS.toMillis(5));
    }
}
