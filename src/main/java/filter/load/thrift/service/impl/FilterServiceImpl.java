package filter.load.thrift.service.impl;

import filter.load.bitMap.SyncRoaringBitmap;
import filter.load.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName : FilterServiceImpl
 * @Description : 过滤服务
 * @Author :
 * @Date: 2020-06-30 16:35
 */
@Service
public class FilterServiceImpl {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FilterServiceImpl.class);

    @Autowired
    private CacheService cacheService;


    public Set<Integer> filter(int key, Set<Integer> userIds) {
        Map<Integer, SyncRoaringBitmap> bitmap = cacheService.getBitmap();
        if (bitmap == null)
            return null;
        if (!bitmap.containsKey(key)) {
            Set<Integer> resultSet = filterFromDB(key, userIds);
            cacheService.addIntoBitMap(key, resultSet);
        }
        Set<Integer> filterFromBitMapSet = filterFromBitMap(key, userIds);
        if (filterFromBitMapSet == null)
            return null;
        return filterFromRedis(key, filterFromBitMapSet);
    }

    private Set<Integer> filterFromRedis(int key, Set<Integer> userIds) {
        return null;
    }

    private Set<Integer> filterFromBitMap(int key, Set<Integer> userIds) {
        return null;
    }

    private Set<Integer> filterFromDB(int key, Set<Integer> userIds) {
        return null;
    }

}
