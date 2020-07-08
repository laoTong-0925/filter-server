package im.cu;


import im.cu.framework.helper.CUBeanFactory;
import im.cu.model.LocalServer;
import im.cu.thrift.Base.ThriftBasedServiceServer;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
import im.cu.thrift.service.handler.Nope31DaysCacheThriftServerHandler;
import im.cu.zk.ZKFactory;

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
    public static void main(String[] args) {
        try {
            int port = 1102;
            new LocalServer("127.0.0.1:" + port);
            Nope31DaysCacheThriftServerHandler handler = CUBeanFactory.getBean(Nope31DaysCacheThriftServerHandler.class);
            handler.init();
            Nope31DaysCacheThriftService.Processor<Nope31DaysCacheThriftServerHandler> processor = new Nope31DaysCacheThriftService.Processor<>(handler);
            new ThriftBasedServiceServer(
                    processor,
                    port,
                    true,
                    true,
                    true);

            TimeUnit.SECONDS.sleep(1);
            ZKFactory.registerHashRingNode(LocalServer.getIp(), LocalServer.getIp());
//            Nope31DaysCacheThriftClient client = CUBeanFactory.getBean(Nope31DaysCacheThriftClient.class);
//            List<Integer> list = new ArrayList<>();
//            list.add(11);
//            list.add(8002);
//            list.add(1232);
//
//            Set<Integer> notExit;
//
//            notExit = client.findExists(15417, list);
//            System.out.println(notExit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
