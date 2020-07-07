package im.cu.thrift.service.handler;

import im.cu.match_vala.cache_v3.thrift.gen.MutableNope31DaysCacheThriftService;
import im.cu.thrift.service.impl.Nope31DaysCacheService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author hongweixu
 * @since 2019-12-19 16:10
 */
@Component
public class MutableNope31DaysCacheThriftServerHandler implements MutableNope31DaysCacheThriftService.Iface {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Nope31DaysCacheService mutableNope31DaysCacheService;


    public void init() {
        mutableNope31DaysCacheService.initFromDB();
    }

    @Override
    public Set<Integer> findExists(int userId, List<Integer> userIds) throws TException {
        try {
            return mutableNope31DaysCacheService.findExists(userId, userIds);
        } catch (Throwable t) {
            logger.info("服务调用失败 findExist, userId=" + userId, t);
            throw new TException(t);
        }
    }
}