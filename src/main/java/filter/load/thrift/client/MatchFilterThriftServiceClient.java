package filter.load.thrift.client;

import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import filter.load.helper.HashRing.HashRingHelper;
import filter.load.model.LocalServer;
import filter.load.model.ServerNode;
import filter.load.route.RouteHelper;
import filter.load.thrift.Base.BaseFilterPoolThriftClient;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.zk.ConfigStringListKeys;
import filter.load.zk.ZKConfigKey;
import filter.load.zk.ZKFactory;
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
import java.util.stream.Collectors;

/**
 * @ClassName : FilterServerClient
 * @Description : 客户端
 * @Author : t_t
 * @Date: 2020-06-28 12:38
 */
@Service
public class MatchFilterThriftServiceClient extends BaseFilterPoolThriftClient<MatchFilterThriftService.Client> {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MatchFilterThriftServiceClient.class);

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
        Map<String, String> realNodeMap = ZKFactory.getAllNode(ZKConfigKey.filterServerPath);
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
     * 获取用户过滤结果,查找不存在
     *
     * @param userId  目标用户
     * @param userIds 物料
     * @return Set<Integer> 过滤后的用户Id
     */
    public Set<Integer> findNotExit(int userId, Set<Integer> userIds) {
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
        MatchFilterThriftService.Iface client = null;
        List<String> serverList = getServicesList().stream().map(this::getUrl).collect(Collectors.toList());
        for (String e : serverList) {
            if (StringUtils.isNotBlank(e) && e.equals(serverNode.getUrl())) {
                String[] serviceStrings = StringUtils.split(e, ":");
                if (serviceStrings.length == 2 && StringUtils.isNotBlank(serviceStrings[0]) && StringUtils.isNotBlank(serviceStrings[1])) {
                    try {
                        client = getClientPool(new ServiceInfo(serviceStrings[0], Integer.parseInt(serviceStrings[1]))).iface();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (client != null) {
            try {
                logger.info("---{}--发出请求----", LocalServer.getIp());
                return client.findNotExist(userId, userIds);
            } catch (TException e) {
                logger.error("FilterServerClient.findNotExit() 调用失败 error", e);
            }
        }
        return null;
    }

    public Set<Integer> findExit(int userId, Set<Integer> userIds) {
        //todo
        return null;
    }


    @Override
    protected ConfigStringListKeys getConfigKey() {
        return ConfigStringListKeys.ThriftMatchFilterServer;
    }

    @Override
    protected MatchFilterThriftService.Client getClient(TTransport transport) {
        return new MatchFilterThriftService.Client(new TBinaryProtocol(new TFramedTransport(transport)));
    }

    @Override
    protected void adjustPoolConfig(PoolConfig poolConfig) {
        poolConfig.setTimeout(5 * 60 * 1000);
    }
}
