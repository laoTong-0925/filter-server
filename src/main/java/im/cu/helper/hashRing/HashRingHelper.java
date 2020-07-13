package im.cu.helper.hashRing;

import im.cu.hash.FnvHashStrategy;
import im.cu.hash.HashStrategy;
import im.cu.model.ServerHashRange;
import im.cu.model.ServerNode;
import im.cu.model.system.LocalServer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName : HashRingHelp
 * @Description : 哈希环
 * @Author : t_t
 * @Date: 2020-06-20 22:36
 */
public class HashRingHelper {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HashRingHelper.class);

    private static final HashStrategy hashStrategy = new FnvHashStrategy();

    /**
     * 虚节点个数，不包含实节点
     */
    private static final int VIRTUAL_NODE_SIZE = 2;

    /**
     * 哈希策略
     *
     * @return HashStrategy
     */
    public static HashStrategy getHashStrategy() {
        return hashStrategy;
    }

    /**
     * 是否加载user数据
     *
     * @param userId              用户id
     * @param serverHashRangeList 服务区间集合
     * @return boolean 加载返回true 不加载返回false
     */
    public static boolean isLoad(int userId, List<ServerHashRange> serverHashRangeList) {
        if (serverHashRangeList == null)
            return false;
        int userHashCode = hashStrategy.getHashCode(String.valueOf(userId));
        for (ServerHashRange range : serverHashRangeList) {
            //一边开，一边闭
            if (range.getLast() && userHashCode > range.getBeforeServerHash()) {
                logger.info(userId + " ----的hash:" + userHashCode + " 大于最后一个节点 " + range.getBeforeServerHash());
                return true;
            } else if (range.getServerHash() >= userHashCode && userHashCode > range.getBeforeServerHash()) {
                logger.info(userId + " ----的hash:" + userHashCode + " 在 " + range.getServerHash() + "~" + range.getBeforeServerHash() + " 命中");
                return true;
            }
        }
        return false;
    }

    /**
     * 加载一致性哈希环
     *
     * @param realNodeList 实节点
     * @return List<HashRingNode> 新哈希环
     */
    public static List<ServerNode> reloadHashRing(List<String> realNodeList, List<Integer> thisServerForHashRing) {
        if (CollectionUtils.isEmpty(realNodeList)) {
            logger.warn("ZK上获取不到过滤服务！！！");
            return null;
        }
        Map<Integer, String> temporaryHasHhRing = new HashMap<>();
        realNodeList.forEach(e -> buildHashRingNode(e, thisServerForHashRing, temporaryHasHhRing));
        logger.info("本服务的节点");
        thisServerForHashRing.forEach(e -> logger.info(e.toString()));
        //排序造环
        return temporaryHasHhRing.entrySet().stream()
                .map(e -> new ServerNode(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ServerNode::getHash))
                .collect(Collectors.toList());
    }

    /**
     * 构建虚节点，并保存本服务的节点
     *
     * @param url                   ip:port
     * @param thisServerForHashRing 哈希环上本服务的节点
     * @return Map<Integer, String>
     */
    private static void buildHashRingNode(String url, List<Integer> thisServerForHashRing, Map<Integer, String> temporaryHasHhRing) {
        int tmp = 0;
        int virtualNodeSize = VIRTUAL_NODE_SIZE;
        while (true) {
            if (tmp > 3000) {
                throw new RuntimeException("buildHashRingNode() 构建哈希环次数 大于3000 存在冲突");
            }
            int serverHashCode = hashStrategy.getHashCode(url + tmp++);
            if (!temporaryHasHhRing.containsKey(serverHashCode)) {//冲突了继续
                temporaryHasHhRing.put(serverHashCode, url);
                if (url.equals(LocalServer.getUrl())) {
                    thisServerForHashRing.add(serverHashCode);
                }
            } else {
                logger.info("{} 数值 hash {} 冲突", url + (tmp - 1), serverHashCode);
                continue;
            }
            if (virtualNodeSize <= 0) {
                return;
            }
            virtualNodeSize--;
        }
    }

    /**
     * 加载服务区间
     */
    public static List<ServerHashRange> initRange(List<ServerNode> sortedHashRing, List<Integer> thisServerForHashRing) {
        List<ServerHashRange> serverHashRangeList = new ArrayList<>();
        if (thisServerForHashRing == null) {
            return serverHashRangeList;
        }
        for (Integer server : thisServerForHashRing) {
            if (null == server) {
                throw new IllegalArgumentException();
            }
            for (int j = 0; j < sortedHashRing.size(); j++) {
                int hashRingHashCode = sortedHashRing.get(j).getHash();
                if (server == hashRingHashCode) {
                    ServerHashRange serverHashRange;
                    if (j != 0) {//其他节点
                        int beforeServerHashCode = sortedHashRing.get(j - 1).getHash();
                        serverHashRange = new ServerHashRange(server, beforeServerHashCode);
                    } else {//首个hash节点 最后一个节点到首个节点
                        serverHashRange = new ServerHashRange(server, 0);
                        ServerHashRange last = new ServerHashRange(Integer.MAX_VALUE,
                                sortedHashRing.get(sortedHashRing.size() - 1).getHash(), true);
                        serverHashRangeList.add(last);
                    }
                    serverHashRangeList.add(serverHashRange);
                }
            }
        }
        return serverHashRangeList;
    }

}
