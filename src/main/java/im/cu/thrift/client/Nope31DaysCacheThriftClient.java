package im.cu.thrift.client;

import com.wealoha.common.config.Config;
import com.wealoha.common.notify.NotifyByZookeeper;
import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import im.cu.base.constants.ConfigStringListKeys;
import im.cu.helper.HashRing.HashRingHelper;
import im.cu.model.ServerNode;
import im.cu.route.RouteHelper;
import im.cu.thrift.Base.BaseFilterPoolThriftClient;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.stereotype.Service;

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

    /**
     * 重载哈希环和连接池
     */
    private void register() {
        NotifyByZookeeper.registerCallback(getConfigKey().configKey(),
                (oldData, newData) -> {
                    //处理新的url数据为List
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<String> list = Config.instance.get(getConfigKey());
                    loadHashRing(list);
                    reloadClientPoolMap();
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
        if (userId == 0 && CollectionUtils.isEmpty(userIds))
            return null;
        if (sortedHashRing == null) {
            synchronized (this) {
                if (sortedHashRing == null) {
                    List<String> list = Config.instance.get(getConfigKey());
                    loadHashRing(list);
                    register();
                }
            }
        }
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
        poolConfig.setTimeout(5 * 60 * 1000);
    }
}
