package im.cu.route;

import im.cu.helper.hashRing.HashRingHelper;
import im.cu.model.ServerNode;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName : RouteHelper
 * @Description : 路由服务,通过哈希从一致性哈希环上获取节点
 * @Author : t_t
 * @Date: 2020-06-30 13:19
 */
public class RouteHelper {

    private static Logger logger = LoggerFactory.getLogger(RouteHelper.class);

    /**
     * 路由
     *
     * @param userId         用户Id
     * @param sortedHashRing 哈希环
     * @return ServerNode 访问的节点
     */
    public static ServerNode routeServer(int userId, List<ServerNode> sortedHashRing) {
        if (CollectionUtils.isEmpty(sortedHashRing))
            return null;
        int hashCode = HashRingHelper.getHashStrategy().getHashCode(String.valueOf(userId));
        int size = sortedHashRing.size();
        for (int i = 0; i < size; i++) {
            ServerNode head = sortedHashRing.get(0);
            ServerNode tail = sortedHashRing.get(size - 1);
            if (head != null && tail != null && (hashCode <= head.getHash() || hashCode > tail.getHash())) {//边界处理
                logger.info("{} 用户的HashCode {} 在  head:" + head.getHash() + "之前  tail:" + tail.getHash() + " 之后 ", userId, hashCode);
                return head;
            } else if (i != 0 && hashCode <= sortedHashRing.get(i).getHash() && hashCode > sortedHashRing.get(i - 1).getHash()) {
                logger.info("{} 用户的HashCode {}在  " + sortedHashRing.get(i).getHash() + " ~ " + sortedHashRing.get(i - 1).getHash() + " 之间", userId, hashCode);
                return sortedHashRing.get(i);
            }
        }
        return null;
    }
}
