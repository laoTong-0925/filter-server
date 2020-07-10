package im.cu;


import im.cu.base.constants.ConfigStringListKeys;
import im.cu.framework.helper.CUBeanFactory;
import im.cu.framework.thrift.ThriftBasedServiceServer;
import im.cu.model.system.LocalServer;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
import im.cu.thrift.service.handler.Nope31DaysCacheThriftServerHandler;

/**
 * @ClassName : TestC
 * @Description :
 * @Author :
 * @Date: 2020-06-24 14:49
 */
public class TestC {

    public static void main(String[] args) {
        try {
            int port = 1103;
            new LocalServer("192.168.0.113:" + port,true);
            Nope31DaysCacheThriftServerHandler handler = CUBeanFactory.getBean(Nope31DaysCacheThriftServerHandler.class);
            handler.init();
            Nope31DaysCacheThriftService.Processor<Nope31DaysCacheThriftServerHandler> processor = new Nope31DaysCacheThriftService.Processor<>(handler);
            new ThriftBasedServiceServer(ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2,
                    processor,
                    port,
                    true,
                    true,
                    true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
