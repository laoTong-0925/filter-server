package im.cu.thrift.service.impl;

import com.ecyrd.speed4j.StopWatch;
import com.github.phantomthief.util.CursorIterator;
import com.wealoha.common.notify.KillSignalManager;
import im.cu.framework.helper.PerfHelper;
import im.cu.framework.perf.MetricHelper;
import im.cu.framework.perf.ReloadableTimer;
import im.cu.match.helper.NopeLoadHelper;
import im.cu.match.recent.relation.cache.bitmap.SyncRoaringBitmap;
import im.cu.match_vala.constant.RelationEdge;
import im.cu.match_vala.dao.RecentRelationRedisDAO;
import im.cu.service.CacheService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author hongweixu
 * @since 2019-12-19 16:49
 */
@Service
public class Nope31DaysCacheService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String prefix = "mutable-nope31";

    private final LinkedBlockingDeque<Integer> asyncLoadingQueue = new LinkedBlockingDeque<>();
    private final ScheduledExecutorService asyncLoadScheduledExecutor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService newDayDataLoadScheduledExecutor = Executors.newScheduledThreadPool(1);

    private ConcurrentHashMap<Integer, SyncRoaringBitmap> inMemoryData;

    @Autowired
    private RecentRelationRedisDAO recentRelationRedisDAO;

    @Autowired
    private NopeLoadHelper nopeLoadHelper;

    @Autowired
    private CacheService cacheService;

    public void initFromDB() {
        inMemoryData = cacheService.loadToBitMap();
        logger.info("数据加载完毕, totalSize={}", inMemoryData.size());

        asyncLoadScheduledExecutor.scheduleAtFixedRate(this::asyncLoad, 10, 10, TimeUnit.SECONDS);
        logger.info("启动定时 未命中用户异步加载 队列加载线程");

        newDayDataLoadScheduledExecutor.scheduleAtFixedRate(() -> {
            loadOldDataAndPurge();

            loadNewDay();
        }, 12, 12, TimeUnit.HOURS);
        logger.info("启动定时 新数据加载线程");

        KillSignalManager.registerTermCallback(() -> {

            asyncLoadScheduledExecutor.shutdown();
            asyncLoadScheduledExecutor.awaitTermination(10, TimeUnit.MINUTES);

            newDayDataLoadScheduledExecutor.shutdown();
            newDayDataLoadScheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
        });
    }

    public Set<Integer> findExists(int userId, Collection<Integer> userIds) {
        StopWatch stopWatch = PerfHelper.getStopWatch("MutableNope31DaysCacheService.findExists." + prefix);
        try (ReloadableTimer.Context ignored = MetricHelper.getTimer("mutable_nope_31_find_exists").time()) {
            SyncRoaringBitmap bitmap = inMemoryData.get(userId);
            if (bitmap == null) {
                bitmap = initFromRedis(userId);
                // 异步单线程补全用户近期31天的数据
                asyncLoadingQueue.addLast(userId);
            }
            return userIds.stream().filter(bitmap::contains).collect(Collectors.toSet());
        } finally {
            stopWatch.stop();
        }
    }

    private SyncRoaringBitmap initFromRedis(int userId) {
        StopWatch stopWatch = PerfHelper.getStopWatch("MutableNope31DaysCacheService.initFromRedis");
        try {
            SyncRoaringBitmap bitmap = new SyncRoaringBitmap();
            new CursorIterator<Integer, Integer>(
                    (c, l) -> recentRelationRedisDAO.find(RelationEdge.Nope, userId, c, l),
                    null, 200, e -> e)
                    .forEach(bitmap::checkedAdd);

            synchronized (String.valueOf(userId).intern()) {
                SyncRoaringBitmap syncRoaringBitmap = inMemoryData.get(userId);
                if (syncRoaringBitmap == null) {
                    inMemoryData.put(userId, bitmap);
                } else {
                    bitmap.forEach(syncRoaringBitmap::checkedAdd);
                }
            }
            return bitmap;
        } finally {
            stopWatch.stop();
        }
    }

    private void loadOldDataAndPurge() {
        Date date = DateUtils.addDays(new Date(), -32);
        logger.info("加载过去32一天的数据, date={}", DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        long start = System.currentTimeMillis();
        nopeLoadHelper.loadRecentNopeByDay(date, umr -> {
            int userId = umr.getUserId();
            int toUserId = umr.getToUserId();
            synchronized (String.valueOf(userId).intern()) {
                SyncRoaringBitmap bitmap = inMemoryData.get(userId);
                if (bitmap != null) {
                    bitmap.checkedRemove(toUserId);
                }
            }
        });
        logger.info("加载过去32一天的数据, totalCost={}", System.currentTimeMillis() - start);
    }

    private void loadNewDay() {
        Date startDate = DateUtils.addDays(new Date(), -1);
        logger.info("加载近期一天的数据, date={}", DateFormatUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
        long start = System.currentTimeMillis();
        nopeLoadHelper.loadRecentNope(startDate, umr -> {
            int userId = umr.getUserId();
            int toUserId = umr.getToUserId();
            synchronized (String.valueOf(userId).intern()) {
                SyncRoaringBitmap bitmap = inMemoryData.get(userId);
                if (bitmap == null) {
                    bitmap = new SyncRoaringBitmap();
                    inMemoryData.put(userId, bitmap);
                }
                bitmap.checkedAdd(toUserId);
            }
        });
        logger.info("加载近期一天的数据, totalCost={}", System.currentTimeMillis() - start);
    }

    /**
     * 异步加载
     */
    private void asyncLoad() {
        if (asyncLoadingQueue.size() == 0) {
            return;
        }
        HashSet<Integer> uniqueIds = new HashSet<>(asyncLoadingQueue.size());
        asyncLoadingQueue.drainTo(uniqueIds);

        logger.info("消费异步加载队列数据, uniqueIds.size={}", uniqueIds.size());

        Date today = new Date();
        long start = today.getTime();
        Date startDate = DateUtils.addDays(today, -31);
        for (Integer userId : uniqueIds) {
            StopWatch stopWatch = PerfHelper.getStopWatch("MutableNope31DaysCacheService.asyncLoad");
            try (ReloadableTimer.Context ignored = MetricHelper.getTimer("mutable_nope_31_async_load").time()) {
                synchronized (String.valueOf(userId).intern()) {
                    SyncRoaringBitmap bitmap = inMemoryData.get(userId);
                    if (bitmap == null) {
                        bitmap = new SyncRoaringBitmap();
                        inMemoryData.put(userId, bitmap);
                    }

                    SyncRoaringBitmap finalBitmap = bitmap;
                    nopeLoadHelper.loadRecentNopeByUser(startDate, userId, umr -> finalBitmap.checkedAdd(umr.getToUserId()));
                }
            } finally {
                stopWatch.stop();
            }
        }
        logger.info("消费异步加载队列数据, uniqueIds.size={}, totalCost={}", uniqueIds.size(), System.currentTimeMillis() - start);
    }
}
