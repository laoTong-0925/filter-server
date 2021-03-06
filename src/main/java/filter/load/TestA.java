package filter.load;

import filter.load.context.CUBeanFactory;
import filter.load.model.LocalServer;
import filter.load.service.CacheService;
import filter.load.thrift.Base.ThriftBasedServiceServer;
import filter.load.thrift.client.MatchFilterThriftServiceClient;
import filter.load.thrift.service.MatchFilterThriftService;
import filter.load.thrift.service.impl.MatchFilterThriftServiceHandler;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : TestA
 * @Description :
 * @Author :
 * @Date: 2020-06-20 10:37
 */
public class TestA {


    public static void main(String[] args) throws Exception {
        InetAddress ip4 = Inet4Address.getLocalHost();
        System.out.println(ip4.getHostAddress());

        int port = 1101;
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

