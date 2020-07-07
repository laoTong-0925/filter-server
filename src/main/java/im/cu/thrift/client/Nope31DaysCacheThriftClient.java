package im.cu.thrift.client;

import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import im.cu.helper.HashRing.HashRingHelper;
import im.cu.match_vala.cache_v3.thrift.gen.MutableNope31DaysCacheThriftService;
import im.cu.model.LocalServer;
import im.cu.model.ServerNode;
import im.cu.route.RouteHelper;
import im.cu.thrift.Base.BaseFilterPoolThriftClient;
import im.cu.zk.ConfigStringListKeys;
import im.cu.zk.ZKConfigKey;
import im.cu.zk.ZKFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName : FilterServerClient
 * @Description : 客户端
 * @Author : t_t
 * @Date: 2020-06-28 12:38
 */
@Service
public class Nope31DaysCacheThriftClient extends BaseFilterPoolThriftClient<MutableNope31DaysCacheThriftService.Client> {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Nope31DaysCacheThriftClient.class);

    /**
     * 哈希环,响应callBack
     */
    private List<ServerNode> sortedHashRing;

    /**
     * 重载哈希环和连接池
     */
    private void register() {
        ZKFactory.registerCallback(ZKConfigKey.filterServer, () -> {
            loadHashRing();
            reloadClientPoolMap();
        });
    }

    private void loadHashRing() {
        logger.info("client 开始加载哈希环  构建RPC连接池");
        Map<String, String> realNodeMap = ZKFactory.getAllNode(ZKConfigKey.filterServerNopePath);
        if (realNodeMap == null) {
            logger.warn("ZK获取不到过滤服务！！！");
            return;
        }
        logger.info("从ZK获取过滤服务节点为:{}", realNodeMap);
        List<Integer> thisServerForHashRing = new ArrayList<>();
        sortedHashRing = HashRingHelper.reloadHashRing(realNodeMap, thisServerForHashRing);
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
                    loadHashRing();
                    register();
                }
            }
        }
        ServerNode serverNode = RouteHelper.routeServer(userId, sortedHashRing);
        if (serverNode == null)
            return null;
        System.out.println("客户端获得调用节点 " + serverNode.getUrl());
        String[] split = StringUtils.split(serverNode.getUrl());
        if (split.length != 2 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])) {
            logger.warn("user {} 获取到的服务存在问题 severNode:{}", userId, serverNode);
            return null;
        }
        MutableNope31DaysCacheThriftService.Iface client = getClientPool(new ServiceInfo(split[0], Integer.parseInt(split[1]))).iface();
        if (client == null) {
            logger.warn("获取到空的client userId:{}", userId);
            return null;
        }
        try {
            logger.info("---{}--发出请求----", LocalServer.getIp());
            return client.findExists(userId, userIds);
        } catch (TException e) {
            logger.error("FilterServerClient.findNotExit() 调用失败 error", e);
        }
        return null;
    }


    @Override
    protected ConfigStringListKeys getConfigKey() {
        return ConfigStringListKeys.ThriftMutableNope31DaysCacheServer;
    }

    @Override
    protected MutableNope31DaysCacheThriftService.Client getClient(TTransport transport) {
        return new MutableNope31DaysCacheThriftService.Client(new TBinaryProtocol(new TFramedTransport(transport)));
    }

    @Override
    protected void adjustPoolConfig(PoolConfig poolConfig) {
        poolConfig.setTimeout(5 * 60 * 1000);
    }
}
