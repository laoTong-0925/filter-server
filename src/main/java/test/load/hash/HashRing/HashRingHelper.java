package test.load.hash.HashRing;

import test.load.LoadCache;
import test.load.hash.CRCHashStrategy;
import test.load.hash.HashStrategy;
import test.load.model.HashRingNode;
import test.load.model.ServerHashRange;
import test.load.zk.ZKConfigKey;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName : HashRingHelp
 * @Description : 哈希环
 * @Author :
 * @Date: 2020-06-20 22:36
 */
public class HashRingHelper {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HashRingHelper.class);

    /**
     * 临时存放节点,方便进行哈希去重
     */
    private static Map<Integer, String> hashRing = new HashMap<>();

    private static final HashStrategy hashStrategy = new CRCHashStrategy();

    private static Integer REGISTER_NODE_SIZE = ZKConfigKey.REGISTER_NODE_SIZE;

    private HashRingHelper() {
    }

    private static class ServerHashRing {
        private static final HashRingHelper INSTANCE = new HashRingHelper();
    }

    public static HashRingHelper getInstance() {
        return ServerHashRing.INSTANCE;
    }

    public static HashStrategy getHashStrategy() {
        return hashStrategy;
    }

    /**
     * 获取服务节点
     *
     * @param key
     * @param sortedHashRing
     * @return HashRingNode 服务节点
     */
    public HashRingNode get(int key, List<HashRingNode> sortedHashRing) {
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
    public boolean isLoad(int userId, List<ServerHashRange> serverHashRangeList) {
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
     * @param sortedHashRing 哈希环
     * @return List<HashRingNode> 新哈希环
     */
    public List<HashRingNode> reloadHashRing(Map<String, String> realNodeMap, List<HashRingNode> sortedHashRing, List<HashRingNode> thisServerForHashRing) {
        clear(thisServerForHashRing, sortedHashRing);
        if (realNodeMap == null) {
            logger.warn("ZK上获取不到过滤服务！！！");
            return null;
        }
        logger.info("获取所有实节点");
        logger.info(realNodeMap.toString());
        realNodeMap.forEach((key, value) -> buildHashRingNode(key, value, thisServerForHashRing));
        logger.info("本服务的节点");
        thisServerForHashRing.forEach(e -> {
            logger.info(e.toString());
        });
        //排序造环
        sortedHashRing = hashRing.entrySet().stream()
                .map(e -> new HashRingNode(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(HashRingNode::getHash))
                .collect(Collectors.toList());
        hashRing.clear();
        return sortedHashRing;
    }

    /**
     * 清理数据
     *
     * @param thisServerForHashRing 本服务的哈希环节点
     * @param sortedHashRing        完整哈希环
     */
    private void clear(List<HashRingNode> thisServerForHashRing, List<HashRingNode> sortedHashRing) {
        if (thisServerForHashRing != null)
            thisServerForHashRing.clear();
        if (sortedHashRing != null)
            sortedHashRing.clear();
    }

    /**
     * 构建虚节点，并保存本服务的节点
     *
     * @param url                   ip:port
     * @param data                  ip:port
     * @param thisServerForHashRing 哈希环上本服务的节点
     */
    private void buildHashRingNode(String url, String data, List<HashRingNode> thisServerForHashRing) {
        for (int i = 0; i <= 100; i++) {
            int serverHashCode = hashStrategy.getHashCode(url + i);
            if (!hashRing.containsKey(serverHashCode)) {//冲突了继续
                hashRing.put(serverHashCode, data);
                if (data.equals(LoadCache.getUrl())) {
                    thisServerForHashRing.add(new HashRingNode(serverHashCode, data));
                }
            } else {
                continue;
            }
            if (REGISTER_NODE_SIZE <= 0) {
                REGISTER_NODE_SIZE = ZKConfigKey.REGISTER_NODE_SIZE;
                return;
            }
            REGISTER_NODE_SIZE--;
        }
    }

    /**
     * 加载服务区间
     */
    public List<ServerHashRange> initRange(List<HashRingNode> sortedHashRing, List<HashRingNode> thisServerForHashRing) {
        List<ServerHashRange> serverHashRangeList = new ArrayList<>();
        if (thisServerForHashRing == null) {
            return serverHashRangeList;
        }
        for (HashRingNode server : thisServerForHashRing) {
            if (null == server) {
                throw new IllegalArgumentException();
            }
            for (int j = 0; j < sortedHashRing.size(); j++) {
                int hashRingHashCode = sortedHashRing.get(j).getHash();
                if (server.getHash() == hashRingHashCode) {
                    ServerHashRange serverHashRange;
                    if (j != 0) {//其他节点
//                        if (j == hashRing.size() - 1) //最后一个已经在第一个中计算了
//                            break;
                        Integer beforeServerHashCode = sortedHashRing.get(j - 1).getHash();
                        if (null == beforeServerHashCode) {
                            throw new IllegalArgumentException();
                        }
                        serverHashRange = new ServerHashRange(server.getHash(), beforeServerHashCode);
                    } else {//首个hash节点 最后一个节点到首个节点
                        serverHashRange = new ServerHashRange(server.getHash(), 0);
                        ServerHashRange last = new ServerHashRange(Integer.MAX_VALUE,
                                sortedHashRing.get(sortedHashRing.size() - 1).getHash(), true, sortedHashRing.get(0).getHash());
                        serverHashRangeList.add(last);
                    }
                    serverHashRangeList.add(serverHashRange);
                }
            }
        }
        logger.info("--------服务区间加载完毕--------");
        serverHashRangeList.forEach(e -> {
            System.out.println(e.toString());
        });
        return serverHashRangeList;
    }

}
