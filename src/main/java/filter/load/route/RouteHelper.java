package filter.load.route;

import filter.load.model.ServerNode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @ClassName : RouteHelper
 * @Description : 路由服务,通过哈希从一致性哈希环上获取节点
 * @Author : t_t
 * @Date: 2020-06-30 13:19
 */
public class RouteHelper {
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
}
