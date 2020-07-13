package im.cu.service;

import im.cu.framework.concurrent.ExecutorsEx;
import im.cu.framework.helper.CUBeanFactory;
import im.cu.framework.helper.LogbackConfigHelper;
import im.cu.match.helper.NopeLoadHelper;
import im.cu.match.recent.relation.cache.bitmap.SyncRoaringBitmap;
import im.cu.match_vala.cache.disk.StoreHelper;
import im.cu.match_vala.cache.disk.StoreMeta;
import im.cu.match_vala.cache.disk.exception.StoreMetaPersistFailException;
import im.cu.model.system.LocalServer;
import im.cu.service.impl.RangeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 加载数据到磁盘,新增的服务则输入beginList 为""
 */
public class Nope31DaysCacheWriterRunner {

    public static void main(String[] args) throws ParseException {
        LogbackConfigHelper.initLogHome("/data/log/cu-filter");
        Logger logger = LoggerFactory.getLogger(Nope31DaysCacheWriterRunner.class);

        if (args == null || args.length == 0) {//192.168.0.113
            System.out.println("请正确配置参数后启动, command [startDate] [endDate] [ip] [port] [beginList]");
            System.exit(0);
        }
        int port = Integer.parseInt(args[3]);

        if (StringUtils.isNotBlank(args[4])) {
            String[] split = StringUtils.split(args[4], ",");
            List<String> beginList = new ArrayList<>();
            Collections.addAll(beginList, split);
            new LocalServer(args[2] + ":" + port, beginList);
        } else {
            new LocalServer(args[2] + ":" + port);
        }
        ExecutorService executorService = ExecutorsEx.newFixedThreadPool(8);
        String prefix = "mutable-nope31";
        Date startDate = DateUtils.parseDate(args[0], "yyyyMMdd");
        Date endDate = DateUtils.parseDate(args[1], "yyyyMMdd");

        RangeService rangeService = CUBeanFactory.getBean(RangeService.class);
        NopeLoadHelper nopeLoadHelper = CUBeanFactory.getBean(NopeLoadHelper.class);

        //手动加载区间
        rangeService.loadHashRange(false);
        ConcurrentHashMap<Integer, SyncRoaringBitmap> dataMap = new ConcurrentHashMap<>(60_0000);

        nopeLoadHelper.loadByDateRange(startDate, endDate,
                umr -> {
                    if (rangeService.isLoad(umr.getUserId())) {
                        int toUserId = umr.getToUserId();
                        int userId = umr.getUserId();
                        SyncRoaringBitmap bitmap = dataMap.get(userId);
                        if (bitmap == null) {
                            synchronized (dataMap) {
                                bitmap = new SyncRoaringBitmap();
                                dataMap.put(userId, bitmap);
                            }
                        }
                        bitmap.checkedAdd(toUserId);
                    }
                }, executorService);

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // ignore
        }

        StoreMeta meta = new StoreMeta(prefix, 0, System.currentTimeMillis());
        try {
            StoreHelper.saveMeta(meta);
        } catch (StoreMetaPersistFailException e) {
            //
            logger.info("保存meta信息失败，跳过, meta={}", meta);
        }
        try {
            System.out.println(dataMap.size());
            StoreHelper.storeBitmap(meta, dataMap);
        } catch (IOException e) {
            // store
            logger.info("数据写入失败", e);
        }
        int size = dataMap.size();
        logger.info("数据写入完毕, totalSize={}", size);

        System.exit(0);
    }
}
