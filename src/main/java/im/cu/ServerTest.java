package im.cu;


import im.cu.base.constants.ConfigStringListKeys;
import im.cu.framework.helper.CUBeanFactory;
import im.cu.framework.thrift.ThriftBasedServiceServer;
import im.cu.model.system.LocalServer;
import im.cu.thrift.service.Nope31DaysCacheThriftService;
import im.cu.thrift.service.handler.Nope31DaysCacheThriftServerHandler;

/**
 * @ClassName : TestA
 * @Description :
 * @Author :
 * @Date: 2020-06-20 10:37
 */
public class ServerTest {


    public static void main(String[] args) {
        if (args == null || args.length != 3 || args[0] == null || args[1] == null || args[2] == null) {
            System.out.println("输入参数 [ip] [port] [isNew] ");
            return;
        }
        int port = Integer.parseInt(args[1]);
        String url = args[0] + ":" + port;
        ConfigStringListKeys stringListKeys = ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2;
        if (Boolean.parseBoolean(args[2])) {
            new LocalServer(url, true);
        } else {
            new LocalServer(url);
        }
        Nope31DaysCacheThriftServerHandler handler = CUBeanFactory.getBean(Nope31DaysCacheThriftServerHandler.class);
        //加载缓存来自磁盘
        handler.init();
        Nope31DaysCacheThriftService.Processor<Nope31DaysCacheThriftServerHandler> processor = new Nope31DaysCacheThriftService.Processor<>(handler);
        new ThriftBasedServiceServer(stringListKeys,
                processor,
                port,
                true,
                true,
                true);
    }
}

