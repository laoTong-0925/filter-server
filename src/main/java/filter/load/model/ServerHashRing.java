package filter.load.model;

import java.util.List;

/**
 * @ClassName : ServerHashRIng
 * @Description : 服务的哈希环、区间、本服务集合
 * @Author : t_t
 * @Date: 2020-06-28 12:55
 */
public class ServerHashRing {
    /**
     * 服务区间
     */
    private List<ServerHashRange> serverHashRangeList;

    /**
     * 哈希环
     */
    private List<HashRingNode> sortedHashRing;

    /**
     * 本服务的哈希,在构建架服务区间时使用
     */
    private List<Integer> thisServerForHashRing;

    public ServerHashRing(List<ServerHashRange> serverHashRangeList, List<HashRingNode> sortedHashRing, List<Integer> thisServerForHashRing) {
        this.serverHashRangeList = serverHashRangeList;
        this.sortedHashRing = sortedHashRing;
        this.thisServerForHashRing = thisServerForHashRing;
    }

    public List<ServerHashRange> getServerHashRangeList() {
        return serverHashRangeList;
    }

    public List<HashRingNode> getSortedHashRing() {
        return sortedHashRing;
    }

    @Override
    public String toString() {
        return "ServerHashRing{" +
                "serverHashRangeList=" + serverHashRangeList +
                ", sortedHashRing=" + sortedHashRing +
                ", thisServerForHashRing=" + thisServerForHashRing +
                '}';
    }
}
