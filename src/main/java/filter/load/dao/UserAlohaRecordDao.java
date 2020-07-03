package filter.load.dao;

import com.wealoha.common.tuple.TwoTuple;
import filter.load.model.UserMatchRecord;
import filter.load.thrift.Base.AlohaType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserAlohaRecordDao {


    /**
     * 过滤指定集合中命中的部分
     *
     * @param type          类型
     * @param userId        当前用户
     * @param targetUserIds 需要过滤的用户
     */
    Set<Integer> filterMatchUsers(int userId, Collection<Integer> targetUserIds);

    /**
     * 查找所有符合条件的数据对
     */
    List<Integer> findAllByUser(AlohaType type, int userId);
}