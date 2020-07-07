//package filter.load.helper;
//
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.spi.JoranException;
//import org.slf4j.ILoggerFactory;
//import org.slf4j.LoggerFactory;
//
//public final class LogbackConfigHelper {
//
//    public static final void initLogHome(String logHomeDirectory) {
//        System.setProperty("LOG_HOME", logHomeDirectory);
//        JoranConfigurator joranConfigurator = new JoranConfigurator();
//        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
//        if (loggerFactory instanceof LoggerContext) {
//            // 正常的环境
//            LoggerContext context = (LoggerContext) loggerFactory;
//            joranConfigurator.setContext(context);
//            context.reset();
//            try {
//                joranConfigurator.doConfigure(
//                        LogbackConfigHelper.class.getClassLoader().getResource("logback/tools.xml"));
//            } catch (JoranException e) {
//                e.printStackTrace();
//            }
//        } else {
//            // 可能是在spark上运行
//            // TODO impl.log4j
//            System.out.println("不识别的log环境: " + loggerFactory.getClass().getName());
//            System.err.println("不识别的log环境: " + loggerFactory.getClass().getName());
//        }
//    }
//}