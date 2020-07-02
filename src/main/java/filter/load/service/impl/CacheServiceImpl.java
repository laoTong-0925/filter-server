package filter.load.service.impl;

import filter.load.MatchFilterThriftServer;
import filter.load.bitMap.SyncRoaringBitmap;
import filter.load.helper.HashRing.HashRingHelper;
import filter.load.model.LocalServer;
import filter.load.model.ServerHashRange;
import filter.load.model.ServerNode;
import filter.load.service.CacheService;
import filter.load.zk.ZKFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName : LoadCacheHelper
 * @Description :
 * @Author :
 * @Date: 2020-06-20 00:37
 */
@Service
public class CacheServiceImpl implements CacheService {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CacheServiceImpl.class);

    /**
     * 服务区间
     */
    private List<ServerHashRange> serverHashRangeList;
    /**
     * bitMap 缓存 <userId, userId对应的Aloha记录的BitMap>
     */
    private Map<Integer, SyncRoaringBitmap> bitmap;

//    @Autowired
//    private Dao;

    @Override
    public Map<Integer, SyncRoaringBitmap> getBitmap() {
        return bitmap;
    }

    @Override
    public void addIntoBitMap(int userId, Collection<Integer> userIds) {
        SyncRoaringBitmap syncRoaringBitmap = new SyncRoaringBitmap();
        userIds.forEach(syncRoaringBitmap::checkedAdd);
        bitmap.put(userId, syncRoaringBitmap);
    }

    /**
     * 加载至BitMap
     */
    @Override
    public void loadToBitMap() {
        Map<String, String> beginNode = MatchFilterThriftServer.beginNode;

        //哈希环
        System.out.println("-------loadServerRange------");
        if (beginNode == null)
            return;
        //本服务hash集合
        List<Integer> thisServerForHashRing = new ArrayList<>();
        //哈希环
        List<ServerNode> sortedHashRingList = HashRingHelper.reloadHashRing(beginNode, thisServerForHashRing);
        //区间
        serverHashRangeList = HashRingHelper.initRange(sortedHashRingList, thisServerForHashRing);
        logger.info("--------服务区间加载完毕--------");
        serverHashRangeList.forEach(e -> logger.info(e.toString()));
        bitmap = new HashMap<>();
        List<Integer> users = Arrays.asList(MatchFilterThriftServer.users);
        //todo 加载数据
        users.parallelStream().filter(this::isLoad).forEach(e -> {
            logger.info("加载用户：" + e.toString());
            List<Integer> alohaRecd = new ArrayList<>(Arrays.asList(MatchFilterThriftServer.Aloha));
            //todo 查出记录
            SyncRoaringBitmap syncRoaringBitmap = new SyncRoaringBitmap();
            for (Integer integer : alohaRecd) {
                syncRoaringBitmap.checkedAdd(integer);
            }
            bitmap.put(e, syncRoaringBitmap);
        });
        ZKFactory.registerHashRingNode(LocalServer.getIp(), LocalServer.getIp());
    }

    /**
     * 判断是否需要加载
     *
     * @param userId 用户Id
     * @return boolean
     */
    private boolean isLoad(int userId) {
        if (serverHashRangeList == null)
            return false;
        return HashRingHelper.isLoad(userId, serverHashRangeList);
    }

}
