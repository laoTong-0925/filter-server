package filter.load;

import filter.load.context.CUBeanFactory;
import filter.load.service.CacheService;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.thrift.service.impl.MatchFilterThriftServiceHandler;
import filter.load.zk.ConfigStringListKeys;
import im.cu.framework.thrift.ThriftBasedServiceServer;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : MatchFilterThriftServer
 * @Description : 启动类
 * @Author : t_t
 * @Date: 2020-07-02 14:40
 */
public class MatchFilterThriftServer {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MatchFilterThriftServer.class);

    public static Integer[] users = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900};
    public static Integer[] Aloha = {1, 10};
    public static Map<String, String> beginNode = new HashMap<>();

    static {
        beginNode.put("192.168.1.1:1100", "192.168.1.1:1100");
        beginNode.put("192.168.1.1:1101", "192.168.1.1:1101");
        beginNode.put("192.168.1.1:1102", "192.168.1.1:1102");
    }

    public static void main(String[] args) {
//        LogbackConfigHelper.initLogHome("/data/log/cu-tmp");
        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
        MatchFilterThriftServiceHandler handler = CUBeanFactory.getBean(MatchFilterThriftServiceHandler.class);
        cacheService.loadToBitMap();
        MatchFilterThriftService.Processor<MatchFilterThriftServiceHandler> processor = new MatchFilterThriftService.Processor<>(handler);
        int port = 16090;


    }

}
