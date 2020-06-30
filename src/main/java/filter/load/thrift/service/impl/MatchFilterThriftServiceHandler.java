package filter.load.thrift.service.impl;

import filter.load.thrift.service.MatchFilterThriftService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @ClassName : MatchFilterThriftServiceHandler
 * @Description : MatchFilterThriftServiceClient 的thrift服务
 * @Author :
 * @Date: 2020-06-29 15:17
 */
@Service
public class MatchFilterThriftServiceHandler implements MatchFilterThriftService.Iface {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MatchFilterThriftServiceHandler.class);

    @Autowired
    private FilterServiceImpl filterService;

    @Override
    public Set<Integer> findExist(int userId, Set<Integer> userIds) {
        //todo
        return null;
    }

    @Override
    public Set<Integer> findNotExist(int userId, Set<Integer> userIds) {
        if (userId == 0 && CollectionUtils.isEmpty(userIds))
            return null;
        return filterService.filter(userId, userIds);
    }


}
