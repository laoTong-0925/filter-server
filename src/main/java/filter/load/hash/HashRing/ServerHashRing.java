package filter.load.hash.HashRing;

import filter.load.model.HashRingNode;
import filter.load.model.ServerHashRange;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : ServerHashRIng
 * @Description : 服务的哈希环
 * @Author : t_t
 * @Date: 2020-06-28 12:55
 */
public class ServerHashRing {
    /**
     * 服务区间
     */
    private List<ServerHashRange> serverHashRangeList = new ArrayList<>();

    /**
     * 哈希环
     */
    private List<HashRingNode> sortedHashRing = new ArrayList<>();

    /**
     * 本服务的哈希节点,在构建架服务区间时使用
     */
    private List<HashRingNode> thisServerForHashRing = new ArrayList<>();

    private ServerHashRing() {
    }

    private static class LazyHolder {
        private static final ServerHashRing INSTANCE = new ServerHashRing();
    }

    public static ServerHashRing getInstance() {
        return ServerHashRing.LazyHolder.INSTANCE;
    }

    public List<ServerHashRange> getServerHashRangeList() {
        return serverHashRangeList;
    }

    public List<HashRingNode> getSortedHashRing() {
        return sortedHashRing;
    }

    public List<HashRingNode> getThisServerForHashRing() {
        return thisServerForHashRing;
    }

    public void setServerHashRangeList(List<ServerHashRange> serverHashRangeList) {
        this.serverHashRangeList = serverHashRangeList;
    }

    public void setSortedHashRing(List<HashRingNode> sortedHashRing) {
        this.sortedHashRing = sortedHashRing;
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
