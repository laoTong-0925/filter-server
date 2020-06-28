package filter.load.client;

import filter.load.LoadCacheHelper;
import filter.load.hash.HashRing.HashRingHelper;
import filter.load.model.ServerHashRing;
import filter.load.model.HashRingNode;

import java.util.List;

/**
 * @ClassName : FilterServerClient
 * @Description : 客户端
 * @Author : t_t
 * @Date: 2020-06-28 12:38
 */
public class FilterServerClient {

    private static ServerHashRing serverHashRing = LoadCacheHelper.getServerHashRing();

    /**
     * 返回服务
     */
    public static HashRingNode getServer(int userId) {
        if (serverHashRing == null)
            return null;
        return HashRingHelper.get(userId, serverHashRing.getSortedHashRing());
    }

    /**
     * 获取用户过滤结果
     *
     * @param userId 目标用户
     * @return List<Integer> 过滤后的用户Id
     */
    public static List<Integer> getUserFilterResult(int userId) {
        if (userId == 0) {
            return null;
        }
        return LoadCacheHelper.getUserFilterResult(userId);
    }


}
