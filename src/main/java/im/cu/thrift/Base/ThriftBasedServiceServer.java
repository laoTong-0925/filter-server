package im.cu.thrift.Base;

import im.cu.model.LocalServer;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Thrift多线程Server<br/>
 * <p>
 * <p>
 * 默认线程池大小512<br/>
 * <p>
 * shard和非shard的服务都从这里继承，区别是:
 * shard服务变更通知由后台手工触发，Server增加或者删除以后只更新配置
 *
 * @author javamonk
 * @createTime 2014年7月4日 下午12:05:33
 */
@SuppressWarnings("restriction")
public class ThriftBasedServiceServer {

    private TServer server;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final int port;

    private String ip = LocalServer.getIp();

    private TServerTransport serverTransport;


    /**
     * @param processor
     * @param port
     * @param useFramedTransport
     * @param handleShutdown     委托term信号处理给父类
     * @param autoNotifyReload   服务增加和减少时是否自动发reload通知
     */
    public ThriftBasedServiceServer(TProcessor processor, int port,
                                    boolean useFramedTransport, boolean handleShutdown, boolean autoNotifyReload) {
        this.port = port;

        try {
            serverTransport = new TServerSocket(port);
            Args args = new TThreadPoolServer.Args(serverTransport)
                    .maxWorkerThreads(12)
                    .processor(processor);
            if (useFramedTransport) {
                args.inputTransportFactory(new TFramedTransport.Factory());
                args.outputTransportFactory(new TFramedTransport.Factory());
            }
            logger.info("服务线程数: {}-{}, 监听shutdown: {}，自动通知重载: {}, frameTransport: {}",
                    args.minWorkerThreads, args.maxWorkerThreads, handleShutdown, autoNotifyReload,
                    useFramedTransport);
            server = new TThreadPoolServer(args);
            logger.info("启动服务: {}:{}...", ip, port);

            new Thread(() -> {
                try {
                    server.serve();
                } catch (Exception e) {
                    logger.error("启动服务失败", e);
                }
            }).start();

            TimeUnit.SECONDS.sleep(5);
//            if (handleShutdown) {
//                // shut down
//                sun.misc.Signal.handle(new sun.misc.Signal("TERM"), new sun.misc.SignalHandler() {
//
//                    @Override
//                    public void handle(sun.misc.Signal arg0) {
//                        logger.info("收到停止信号");
//
//                        shutdown(autoNotifyReload);
//
//                        logger.info("fin.");
//                        System.exit(0);
//                    }
//                });
//            }
        } catch (Exception e) {
            throw new RuntimeException("启动服务失败", e);
        }
    }

//    /**
//     * 停止服务
//     */
//    public void shutdown(boolean notify) {
//        logger.info("取消服务注册..");
//        ServiceServerRegistHelper.unregister(configKey, ip, port, notify);
//
//        if (ContextHelper.isUnderTestEnv()) {
//            logger.info("内网不等待了，直接结束");
//        } else {
//            try {
//                logger.info("sleep 30s...");
//                TimeUnit.SECONDS.sleep(30);
//            } catch (InterruptedException e) {
//            }
//        }
//        logger.info("停止服务" + configKey + "...");
//        server.stop();
//        serverTransport.close();
//    }
//
//
//    /**
//     * 重载所有Client的配置
//     *
//     * @param configKey
//     */
//    public static void reload(ConfigStringListKeys configKey) {
//        NotifyByZookeeper.notify(configKey.configKey(), configKey.configKey());
//    }
}
