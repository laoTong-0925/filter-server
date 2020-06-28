package filter.load.hash.HashRing;

import filter.load.LoadCacheHelper;
import filter.load.hash.CRCHashStrategy;
import filter.load.hash.HashStrategy;
import filter.load.model.HashRingNode;
import filter.load.model.ServerHashRange;
import filter.load.zk.ZKConfigKey;

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

    private static final HashStrategy hashStrategy = new CRCHashStrategy();

    private static Integer VIRTUAL_NODE_SIZE = ZKConfigKey.VIRTUAL_NODE_SIZE;

    private HashRingHelper() {
    }

    public static HashStrategy getHashStrategy() {
        return hashStrategy;
    }

    /**
     * 获取服务节点
     *
     * @param key            key
     * @param sortedHashRing 哈希环
     * @return HashRingNode 服务节点
     */
    public static HashRingNode get(int key, List<HashRingNode> sortedHashRing) {
        if (sortedHashRing == null)
            return null;
        int size = sortedHashRing.size();
        for (int i = 0; i < size; i++) {
            HashRingNode head = sortedHashRing.get(0);
            HashRingNode tail = sortedHashRing.get(size - 1);
            if (head != null && tail != null && (key <= head.getHash() || key > tail.getHash())) {//边界处理
                return head;
            } else if (key <= sortedHashRing.get(i).getHash() && key > sortedHashRing.get(i - 1).getHash()) {
                return sortedHashRing.get(i);
            }
        }
        return null;
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
     * @param realNodeMap    实节点
     * @return List<HashRingNode> 新哈希环
     */
    public static List<HashRingNode> reloadHashRing(Map<String, String> realNodeMap, List<Integer> thisServerForHashRing) {
        if (realNodeMap == null) {
            logger.warn("ZK上获取不到过滤服务！！！");
            return null;
        }
        logger.info("获取所有实节点");
        logger.info(realNodeMap.toString());
        Map<Integer, String> temporaryHasHhRing = new HashMap<>();
        realNodeMap.forEach((key, value) -> buildHashRingNode(key, value, thisServerForHashRing, temporaryHasHhRing));
        logger.info("本服务的节点");
        thisServerForHashRing.forEach(e -> logger.info(e.toString()));
        //排序造环
        return temporaryHasHhRing.entrySet().stream()
                .map(e -> new HashRingNode(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(HashRingNode::getHash))
                .collect(Collectors.toList());
    }

    /**
     * 构建虚节点，并保存本服务的节点
     *
     * @param url                   ip:port
     * @param data                  ip:port
     * @param thisServerForHashRing 哈希环上本服务的节点
     */
    private static void buildHashRingNode(String url, String data, List<Integer> thisServerForHashRing, Map<Integer, String> temporaryHasHhRing) {
        for (int i = 0; i <= 100; i++) {
            int serverHashCode = hashStrategy.getHashCode(url + i);
            if (!temporaryHasHhRing.containsKey(serverHashCode)) {//冲突了继续
                temporaryHasHhRing.put(serverHashCode, data);
                if (data.equals(LoadCacheHelper.getUrl())) {
                    thisServerForHashRing.add(serverHashCode);
                }
            } else {
                continue;
            }
            if (VIRTUAL_NODE_SIZE <= 0) {
                VIRTUAL_NODE_SIZE = ZKConfigKey.VIRTUAL_NODE_SIZE;
                return;
            }
            VIRTUAL_NODE_SIZE--;
        }
    }

    /**
     * 加载服务区间
     */
    public static List<ServerHashRange> initRange(List<HashRingNode> sortedHashRing, List<Integer> thisServerForHashRing) {
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
                        Integer beforeServerHashCode = sortedHashRing.get(j - 1).getHash();
                        if (null == beforeServerHashCode) {
                            throw new IllegalArgumentException();
                        }
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
//        logger.info("--------服务区间加载完毕--------");
//        serverHashRangeList.forEach(e -> {
//            System.out.println(e.toString());
//        });
        return serverHashRangeList;
    }

}
