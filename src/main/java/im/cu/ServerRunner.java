package im.cu;


import im.cu.base.constants.ConfigStringListKeys;
import im.cu.framework.helper.CUBeanFactory;
import im.cu.framework.thrift.ThriftBasedServiceServer;
import im.cu.model.system.LocalServer;
import im.cu.thrift.service.gen.Nope31DaysCacheThriftService;
import im.cu.thrift.service.handler.Nope31DaysCacheThriftServerHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName : 服务端 新增的服务则输入beginList 为""
 * @Description :
 * @Author :
 * @Date: 2020-06-20 10:37
 */
public class ServerRunner {


    public static void main(String[] args) {
//        LogbackConfigHelper.initLogHome("/data/log/cu-filter");
        if (args == null || args.length == 0 || StringUtils.isBlank(args[0]) || StringUtils.isBlank(args[1])) {
            System.out.println("输入参数 [ip] [port] [beginList] ");
            System.exit(0);
        }
        int port = Integer.parseInt(args[1]);
        String url = args[0] + ":" + port;
        ConfigStringListKeys stringListKeys = ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2;
        if (StringUtils.isNotBlank(args[2])) {//非新增
            String[] split = StringUtils.split(args[2], ",");

            List<String> beginList = new ArrayList<>();
            Collections.addAll(beginList, split);
            new LocalServer(args[0] + ":" + port, beginList);
        } else {//新增
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

