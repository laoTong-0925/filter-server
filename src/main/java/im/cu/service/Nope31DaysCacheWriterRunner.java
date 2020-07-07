//package filter.load.service;
//
//import im.cu.framework.concurrent.ExecutorsEx;
//import im.cu.framework.helper.CUBeanFactory;
//import im.cu.framework.helper.LogbackConfigHelper;
//import im.cu.match.helper.NopeLoadHelper;
//import im.cu.match.recent.relation.cache.bitmap.SyncRoaringBitmap;
//import im.cu.match_vala.cache.disk.StoreHelper;
//import im.cu.match_vala.cache.disk.StoreMeta;
//import im.cu.match_vala.cache.disk.exception.StoreMetaPersistFailException;
//import org.apache.commons.lang3.time.DateUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.Date;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class Nope31DaysCacheWriterRunner {
//
//    public static void main(String[] args) throws ParseException {
//        LogbackConfigHelper.initLogHome("/data/log/cu-store-immutable-data2disk");
//        Logger logger = LoggerFactory.getLogger(Nope31DaysCacheWriterRunner.class);
//
//        ExecutorService executorService = ExecutorsEx.newFixedThreadPool(8);
//        NopeLoadHelper nopeLoadHelper = CUBeanFactory.getBean(NopeLoadHelper.class);
//        if (args == null || args.length == 0) {
//            System.out.println("请正确配置参数后启动, command [prefix] [startDate] [endDate]");
//            System.exit(0);
//        }
//        // String prefix = "nope-" + args[0] + "-" + args[1];
//        String prefix = args[0];
//        Date startDate = DateUtils.parseDate(args[1], "yyyyMMdd");
//        Date endDate = DateUtils.parseDate(args[2], "yyyyMMdd");
//
//        ConcurrentHashMap<Integer, SyncRoaringBitmap> dataMap = new ConcurrentHashMap<>(60_0000);
//        nopeLoadHelper.loadByDateRange(startDate, endDate,
//                umr -> {
//                    int toUserId = umr.getToUserId();
//                    int userId = umr.getUserId();
//                    SyncRoaringBitmap bitmap = dataMap.get(userId);
//                    if (bitmap == null) {
//                        synchronized (dataMap) {
//                            bitmap = new SyncRoaringBitmap();
//                            dataMap.put(userId, bitmap);
//                        }
//                    }
//                    bitmap.checkedAdd(toUserId);
//                }, executorService);
//
//        executorService.shutdown();
//        try {
//            executorService.awaitTermination(10, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            // ignore
//        }
//
//        StoreMeta meta = new StoreMeta(prefix, 0, System.currentTimeMillis());
//        try {
//            StoreHelper.saveMeta(meta);
//        } catch (StoreMetaPersistFailException e) {
//            //
//            logger.info("保存meta信息失败，跳过, meta={}", meta);
//        }
//        try {
//            StoreHelper.storeBitmap(meta, dataMap);
//        } catch (IOException e) {
//            // store
//            logger.info("数据写入失败", e);
//        }
//        int size = dataMap.size();
//        logger.info("数据写入完毕, totalSize={}", size);
//
//        System.exit(0);
//    }
//}
