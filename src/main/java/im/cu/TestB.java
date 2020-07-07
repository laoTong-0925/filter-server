package im.cu;


import im.cu.framework.helper.CUBeanFactory;
import im.cu.match_vala.cache_v3.thrift.gen.MutableNope31DaysCacheThriftService;
import im.cu.model.LocalServer;
import im.cu.thrift.Base.ThriftBasedServiceServer;
import im.cu.thrift.client.Nope31DaysCacheThriftClient;
import im.cu.thrift.service.handler.MutableNope31DaysCacheThriftServerHandler;

import java.util.ArrayList;
import java.util.List;
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
    public static void main(String[] args) throws Exception {
        try {
            int port = 1102;
            LocalServer localServer = new LocalServer("127.0.0.1:" + port);
            MutableNope31DaysCacheThriftServerHandler handler = CUBeanFactory.getBean(MutableNope31DaysCacheThriftServerHandler.class);
            handler.init();
            MutableNope31DaysCacheThriftService.Processor<MutableNope31DaysCacheThriftServerHandler> processor = new MutableNope31DaysCacheThriftService.Processor<>(handler);
            new ThriftBasedServiceServer(
                    processor,
                    port,
                    true,
                    true,
                    true);

            TimeUnit.SECONDS.sleep(1);
            Nope31DaysCacheThriftClient client = CUBeanFactory.getBean(Nope31DaysCacheThriftClient.class);
            List<Integer> list = new ArrayList<>();
            list.add(111);
            list.add(8002);
            list.add(1);

            Set<Integer> notExit;

            notExit = client.findExists(8001, list);
            System.out.println(notExit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
