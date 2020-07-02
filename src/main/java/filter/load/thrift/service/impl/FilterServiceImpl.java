package filter.load.thrift.service.impl;

import filter.load.bitMap.SyncRoaringBitmap;
import filter.load.dao.UserAlohaRecordDao;
import filter.load.service.CacheService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName : FilterServiceImpl
 * @Description : 过滤服务 record
 * @Author :
 * @Date: 2020-06-30 16:35
 */
@Service
public class FilterServiceImpl {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FilterServiceImpl.class);

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserAlohaRecordDao userAlohaRecordDao;


    public Set<Integer> filter(int key, Set<Integer> userIds) {
        Map<Integer, SyncRoaringBitmap> bitmap = cacheService.getBitmap();
        if (bitmap == null)
            return null;
        Set<Integer> resultSet;
        if (!bitmap.containsKey(key)) {
            resultSet = filterFromDB(key, userIds);
        } else {
            SyncRoaringBitmap syncRoaringBitmap = bitmap.get(key);
            resultSet = filterFromBitMap(userIds, syncRoaringBitmap);
        }
        return resultSet;
    }

    private Set<Integer> filterFromBitMap(Set<Integer> userIds, SyncRoaringBitmap bitmap) {
        if (bitmap.isEmpty())
            return null;
        return userIds.parallelStream().filter(e -> !bitmap.contains(e)).collect(Collectors.toSet());
    }

    private Set<Integer> filterFromDB(int key, Set<Integer> userIds) {
        //查询所有的关系
        List<Integer> userAlohaRecordByUserId = userAlohaRecordDao.getUserAlohaRecordByUserId(key);
        if (CollectionUtils.isEmpty(userAlohaRecordByUserId))
            return null;
        cacheService.addIntoBitMap(key, userAlohaRecordByUserId);
        userIds.removeAll(userAlohaRecordByUserId);
        return userIds;
    }

}
