package filter.load.dao;

import java.util.List;

/**
 * 用户Aloha记录 Dao
 */
public interface UserAlohaRecordDao {
    List<Integer> getUserAlohaRecordByUserId(Integer userId);
}
