package filter.load;

import filter.load.hash.HashRing.HashRingHelper;
import filter.load.hash.HashRing.ServerHashRing;
import filter.load.model.HashRingNode;
import filter.load.model.ServerHashRange;
import filter.load.zk.ZKConfigKey;
import filter.load.zk.ZKFactory;
import org.apache.commons.lang3.StringUtils;

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
public class LoadCacheHelper {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoadCacheHelper.class);

    private static ServerHashRing serverHashRing = ServerHashRing.getInstance();

    //服务区间
//    private static List<ServerHashRange> serverHashRangeList = serverHashRing.getServerHashRangeList();

    //哈希环
//    private static List<HashRingNode> sortedHashRing = serverHashRing.getSortedHashRing();

    /**
     * 本服务的哈希节点,在构建架服务区间时使用
     */
    private static List<HashRingNode> thisServerForHashRing = ServerHashRing.getInstance().getThisServerForHashRing();

    private static int port;
    private static String ip;

    public LoadCacheHelper(int port, String ip) {
        LoadCacheHelper.port = port;
        LoadCacheHelper.ip = ip;
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
            serverHashRing.setSortedHashRing(HashRingHelper.reloadHashRing(allNode, serverHashRing.getSortedHashRing(), thisServerForHashRing));
            logger.info("最新哈希环");
            logger.info(serverHashRing.getSortedHashRing().toString());
            serverHashRing.setServerHashRangeList(HashRingHelper.initRange(serverHashRing.getSortedHashRing(), thisServerForHashRing));
            logger.info("--------服务区间加载完毕--------");
            serverHashRing.getServerHashRangeList().forEach(e -> {
                System.out.println(e.toString());
            });
        }
    }

    /**
     * 判断是否需要加载
     *
     * @param userId
     * @return
     */
    public static boolean isLoad(int userId) {
        return HashRingHelper.isLoad(userId, serverHashRing.getServerHashRangeList());
    }

    /**
     * 获取用户过滤结果
     *
     * @param userId 目标用户
     * @return List<Integer> 过滤后的用户Id
     */
    public static List<Integer> getUserFilterResult(int userId) {
        List<Integer> userIds = new ArrayList<>();
        List<Integer> materielUsers = getMaterielUsers(userId);
        materielUsers.forEach(e -> {
            if (isLoad(userId)) {
                //todo filter BitMap, add()
            } else {
                //todo filter DB, add()
            }
        });
        return userIds;
    }

    private static List<Integer> getMaterielUsers(int userId) {
        List<Integer> materielUsers = new ArrayList<>();
        //todo 获取物料
        return materielUsers;
    }

}