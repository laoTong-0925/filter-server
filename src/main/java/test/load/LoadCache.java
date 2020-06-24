package test.load;

import org.apache.commons.lang3.StringUtils;
import test.load.hash.HashRing.HashRingHelper;
import test.load.model.HashRingNode;
import test.load.model.ServerHashRange;
import test.load.zk.ZKConfigKey;
import test.load.zk.ZKFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : LoadCacheHelper
 * @Description :
 * @Author :
 * @Date: 2020-06-20 00:37
 */
public class LoadCache {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoadCache.class);

    private static HashRingHelper hashRingHelper = HashRingHelper.getInstance();

    //服务区间
    private static List<ServerHashRange> serverHashRangeList = new ArrayList<>();

    //哈希环
    private static List<HashRingNode> sortedHashRing = new ArrayList<>();

    /**
     * 本服务的哈希节点,在构建架服务区间时使用
     */
    private static List<HashRingNode> thisServerForHashRing = new ArrayList<>();

    private static int port;
    private static String ip;

    public LoadCache(int port, String ip) {
        LoadCache.port = port;
        LoadCache.ip = ip;
        loadCacheRegister();

    }

    public static String getUrl() {
        return ip + ":" + port;
    }

    /**
     * 注册，监听
     */
    private void loadCacheRegister() {
        //注册实体节点
        String url = ip + ":" + port;
        int serverHashCode = HashRingHelper.getHashStrategy().getHashCode(url);
        logger.info("注册回调------>" + url);
        ZKFactory.registerCallback("/" + serverHashCode + ZKConfigKey.filterServerNode,
                (path, oldData, newData) -> {
                    if (StringUtils.isNotBlank(path) || StringUtils.isNotBlank(newData)) {
                        if (path.contains(ZKConfigKey.filterServerNode)) {
                            reloadServerRange();
                            try {
                                for (int i = 0; i < TestData.users.length - 1; i++) {
                                    if (isLoad(TestData.users[i])) {
                                        TimeUnit.SECONDS.sleep(1);
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        logger.warn("新过滤服务路径为空或Data为空 path:{} newData:{}", path, newData);
                    }
                });
        ZKFactory.registerHashRingNode(url, url);
        logger.info("注册------>" + url + "  serverHashCode--->" + serverHashCode);
    }

    /**
     * 重载哈希环、服务区间
     */
    private static void reloadServerRange() {
        //哈希环
        System.out.println("-------reloadServerRange------");
        Map<String, String> allNode = ZKFactory.getAllNode(ZKConfigKey.filterServerPath);
        if (allNode != null) {
            sortedHashRing = hashRingHelper.reloadHashRing(allNode, sortedHashRing, thisServerForHashRing);
            logger.info("最新哈希环");
            logger.info(sortedHashRing.toString());
            serverHashRangeList = hashRingHelper.initRange(sortedHashRing, thisServerForHashRing);
        }
    }

    /**
     * 返回服务
     */
    public static HashRingNode getServer(int userId) {
        return hashRingHelper.get(userId, sortedHashRing);
    }


    /**
     * 判断是否需要加载
     *
     * @param userId
     * @return
     */
    public static boolean isLoad(int userId) {
        return hashRingHelper.isLoad(userId, serverHashRangeList);
    }


}
