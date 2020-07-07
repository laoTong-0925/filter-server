//package filter.load.context;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//public abstract class CUBeanFactoryFilter {
//
//    private static ApplicationContext applicationContext;
//
//    private static Object lock = new Object();
//
//    private static ApplicationContext getApplicationContext() {
//        if (applicationContext == null) {
//            synchronized (lock) {
//                if (applicationContext == null) {
//                    applicationContext = new ClassPathXmlApplicationContext(
//                            "classpath*:/applicationContext*.xml");
//                }
//            }
//        }
//        return applicationContext;
//    }
//
//    public static <T> T getBean(Class<T> requiredType) {
//        return getApplicationContext().getBean(requiredType);
//    }
//
//    public static <T> T getBean(String name, Class<T> requiredType) {
//        return getApplicationContext().getBean(name, requiredType);
//    }
//}