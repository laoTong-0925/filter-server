package im.cu;


import im.cu.framework.helper.CUBeanFactory;
import im.cu.model.LocalServer;
import im.cu.thrift.Base.ThriftBasedServiceServer;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
import im.cu.thrift.service.handler.Nope31DaysCacheThriftServerHandler;
import im.cu.zk.ZKFactory;

/**
 * @ClassName : TestC
 * @Description :
 * @Author :
 * @Date: 2020-06-24 14:49
 */
public class TestC {

    public static void main(String[] args) throws Exception {
        try {
            int port = 1103;
            new LocalServer("127.0.0.1:" + port,true);
            Nope31DaysCacheThriftServerHandler handler = CUBeanFactory.getBean(Nope31DaysCacheThriftServerHandler.class);
            handler.init();
            Nope31DaysCacheThriftService.Processor<Nope31DaysCacheThriftServerHandler> processor = new Nope31DaysCacheThriftService.Processor<>(handler);
            new ThriftBasedServiceServer(
                    processor,
                    port,
                    true,
                    true,
                    true);
            ZKFactory.registerHashRingNode(LocalServer.getIp(), LocalServer.getIp());

//            TimeUnit.SECONDS.sleep(1);
//            Nope31DaysCacheThriftClient client = CUBeanFactory.getBean(Nope31DaysCacheThriftClient.class);
//            List<Integer> list = new ArrayList<>();
//            list.add(111);
//            list.add(8002);
//            list.add(1);
//
//            Set<Integer> notExit;
//
//            notExit = client.findExists(8001, list);
//            System.out.println(notExit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
