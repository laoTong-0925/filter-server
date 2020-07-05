package filter.load;

import filter.load.context.CUBeanFactory;
import filter.load.model.LocalServer;
import filter.load.service.CacheService;
import filter.load.thrift.Base.ThriftBasedServiceServer;
import filter.load.thrift.client.MatchFilterThriftServiceClient;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.thrift.service.impl.MatchFilterThriftServiceHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName : TestAddD
 * @Description :
 * @Author :
 * @Date: 2020-07-03 15:27
 */
public class TestAddD {

    public static void main(String[] args) {
        int port = 1104;
        LocalServer localServer = new LocalServer("127.0.0.1:" + port,true);
        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
        MatchFilterThriftServiceHandler handler = CUBeanFactory.getBean(MatchFilterThriftServiceHandler.class);
        cacheService.loadToBitMap();
        MatchFilterThriftService.Processor<MatchFilterThriftServiceHandler> processor = new MatchFilterThriftService.Processor<>(handler);
        MatchFilterThriftServiceClient client = CUBeanFactory.getBean(MatchFilterThriftServiceClient.class);
        int size = cacheService.getBitmap().size();
        System.out.println(size);
        new ThriftBasedServiceServer(
                processor,
                port,
                true,
                true,
                true);

        Set<Integer> set = new HashSet<>();
        set.add(111);
        set.add(8002);
        set.add(1);
        Set<Integer> notExit = null;
        try {
            notExit = client.findNotExit(8001, set);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(notExit);
    }
}
