package im.cu.thrift.service.handler;

import im.cu.match_vala.cache_v3.MutableNope31DaysCacheService;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
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
public class Nope31DaysCacheThriftServerHandler implements Nope31DaysCacheThriftService.Iface {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MutableNope31DaysCacheService mutableNope31DaysCacheService;

    /**
     * 从磁盘加载数据
     */
    public void init() {
        mutableNope31DaysCacheService.initFromDisk();
    }

    @Override
    public Set<Integer> findExists(int userId, List<Integer> userIds) throws TException {
        try {
            logger.info("收到--{}---请求", userId);
            return mutableNope31DaysCacheService.findExists(userId, userIds);
        } catch (Throwable t) {
            logger.info("服务调用失败 findExist, userId=" + userId, t);
            throw new TException(t);
        }
    }
}