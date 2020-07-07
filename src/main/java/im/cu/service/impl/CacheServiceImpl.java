package im.cu.service.impl;

import com.ecyrd.speed4j.StopWatch;
import com.github.phantomthief.util.CursorIterator;
import im.cu.MatchFilterThriftServer;
import im.cu.framework.helper.PerfHelper;
import im.cu.helper.HashRing.HashRingHelper;
import im.cu.match.helper.NopeLoadHelper;
import im.cu.match.recent.relation.cache.bitmap.SyncRoaringBitmap;
import im.cu.model.LocalServer;
import im.cu.model.ServerHashRange;
import im.cu.model.ServerNode;
import im.cu.service.CacheService;
import im.cu.user.dao.UserDAO;
import im.cu.user.model.User;
import im.cu.zk.ZKConfigKey;
import im.cu.zk.ZKFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    @Autowired
    private UserDAO userDao;

    @Autowired
    private NopeLoadHelper nopeLoadHelper;

    /**
     * 加载至BitMap
     */
    @Override
    public ConcurrentHashMap<Integer, SyncRoaringBitmap> loadToBitMap() {
        ConcurrentHashMap<Integer, SyncRoaringBitmap> bitmap = new ConcurrentHashMap<>();
        Map<String, String> beginNode;
        if (LocalServer.getIsNew()) {
            String ip = LocalServer.getIp();
            logger.info("-----------新增服务---{}------", ip);
            beginNode = ZKFactory.getAllNode(ZKConfigKey.filterServerNopePath);
            beginNode.put(ip, ip);
        } else {
            beginNode = MatchFilterThriftServer.beginNode;
        }
        //哈希环
        System.out.println("-------loadServerRange------");
        if (beginNode == null)
            return bitmap;
        //本服务hash集合
        List<Integer> thisServerForHashRing = new ArrayList<>();
        //哈希环
        List<ServerNode> sortedHashRingList = HashRingHelper.reloadHashRing(beginNode, thisServerForHashRing);
        //区间
        serverHashRangeList = HashRingHelper.initRange(sortedHashRingList, thisServerForHashRing);
        int i = serverHashRangeList.stream().mapToInt(e -> (e.getServerHash() - e.getBeforeServerHash())).sum();
        System.out.println("覆盖范围 ：" + (double) i / (double) Integer.MAX_VALUE);
        logger.info("--------服务区间加载完毕--------");
        serverHashRangeList.forEach(e -> logger.info(e.toString()));

        //所有用户
        List<Integer> users = new CursorIterator<>((c, l) -> userDao.getByCursorAsc(c, l), 0, 2000, User::getId)
                .stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
        Date now = new Date();
        users.parallelStream().filter(this::isLoad).forEach(e -> {
            StopWatch stopWatch = PerfHelper.getStopWatch("loadUserToBitMap");
            try {
                logger.info("加载用户：{}", e);
                SyncRoaringBitmap bitmapForNope = new SyncRoaringBitmap();
                Date date = DateUtils.addDays(now, -31);
                while (date.getTime() <= now.getTime()) {
                    nopeLoadHelper.loadRecentNopeByUser(date, e, umr -> bitmapForNope.checkedAdd(umr.getToUserId()));
                    date = DateUtils.addDays(date, 1);
                }
                bitmap.put(e, bitmapForNope);
            } finally {
                stopWatch.stop();
            }
        });
        ZKFactory.registerHashRingNode(LocalServer.getIp(), LocalServer.getIp());
        return bitmap;
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
