package im.cu.thrift.service.handler;

import im.cu.service.impl.Nope31DaysCacheService;
import im.cu.thrift.service.gen.Nope31DaysCacheThriftService;
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
    private Nope31DaysCacheService nope31DaysCacheService;

    /**
     * 从磁盘加载数据
     */
    public void init() {
        nope31DaysCacheService.initFromDisk();
    }

    @Override
    public Set<Integer> findExists(int userId, List<Integer> userIds) throws TException {
        try {
            logger.info("收到--{}---请求", userId);
            return nope31DaysCacheService.findExists(userId, userIds);
        } catch (Throwable t) {
            logger.info("服务调用失败 findExist, userId=" + userId, t);
            throw new TException(t);
        }
    }
}