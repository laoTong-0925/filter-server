package filter.load;

import filter.load.service.CacheService;
import filter.load.thrift.client.MatchFilterThriftServiceClient;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LoadCacheHelperTest {


    @Test
    public void testA() throws InterruptedException {
        MatchFilterThriftServiceClient client = CUBeanFactory.getBean(MatchFilterThriftServiceClient.class);
        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
        cacheService.loadToBitMap();
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(100);
        set.add(2);
        Set<Integer> notExit = client.findNotExit(600, set);

        System.out.println(notExit);
        TimeUnit.MINUTES.sleep(5);
    }

    @Test
    public void testB() throws InterruptedException {
        MatchFilterThriftServiceClient client = CUBeanFactory.getBean(MatchFilterThriftServiceClient.class);
        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
        cacheService.loadToBitMap();
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(100);
        set.add(2);
        Set<Integer> notExit = client.findNotExit(600, set);

        System.out.println(notExit);
        TimeUnit.MINUTES.sleep(5);
    }
}
