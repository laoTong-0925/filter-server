package filter.load.thrift.client;

import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import filter.load.model.ServerNode;
import filter.load.route.RouteHelper;
import filter.load.thrift.Base.BaseFilterPoolThriftClient;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.zk.ConfigStringListKeys;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
     * 获取用户过滤结果,查找不存在
     *
     * @param userId  目标用户
     * @param userIds 物料
     * @return Set<Integer> 过滤后的用户Id
     */
    public Set<Integer> findNotExit(int userId, Set<Integer> userIds) {
        if (userId == 0 && CollectionUtils.isEmpty(userIds))
            return null;
        List<ServerNode> sortedHashRing = RouteHelper.getSortedHashRing();
        ServerNode serverNode = RouteHelper.routeServer(userId, sortedHashRing);
        if (serverNode == null)
            return null;
        MatchFilterThriftService.Iface client = null;
        for (String e : getOldServicesListCache()) {
            if (StringUtils.isNotBlank(e) && e.equals(serverNode.getUrl())) {
                String[] serviceStrings = StringUtils.split(e, ":");
                if (serviceStrings.length == 2 && StringUtils.isNotBlank(serviceStrings[0]) && StringUtils.isNotBlank(serviceStrings[1])) {
                    client = getClientPool(new ServiceInfo(serviceStrings[0], Integer.parseInt(serviceStrings[1]))).iface();
                }
            }
        }
        if (client != null) {
            try {
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
