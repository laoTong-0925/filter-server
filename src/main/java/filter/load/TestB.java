package filter.load;

import filter.load.context.CUBeanFactory;
import filter.load.model.LocalServer;
import filter.load.service.CacheService;
import filter.load.thrift.Base.ThriftBasedServiceServer;
import filter.load.thrift.client.MatchFilterThriftServiceClient;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.thrift.service.impl.MatchFilterThriftServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : TestB
 * @Description :
 * @Author :
 * @Date: 2020-06-20 11:19
 */
public class TestB {
    /**
     * A B 服务组成的Hash环
     * <p>
     * 0-1562-2028-5691-6093-9816-10158-13945-14223-Integer.MAX
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        int port = 1102;
        LocalServer localServer = new LocalServer("127.0.0.1:" + port);
        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
        MatchFilterThriftServiceHandler handler = CUBeanFactory.getBean(MatchFilterThriftServiceHandler.class);
        cacheService.loadToBitMap();
        MatchFilterThriftService.Processor<MatchFilterThriftServiceHandler> processor = new MatchFilterThriftService.Processor<>(handler);
        int size = cacheService.getBitmap().size();
        System.out.println(size);
        new ThriftBasedServiceServer(
                processor,
                port,
                true,
                true,
                true);

        TimeUnit.SECONDS.sleep(1);
        MatchFilterThriftServiceClient client = CUBeanFactory.getBean(MatchFilterThriftServiceClient.class);
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
