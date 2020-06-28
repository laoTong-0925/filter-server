package filter.load;

import filter.load.model.ServerHashRing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName : TestA
 * @Description :
 * @Author :
 * @Date: 2020-06-20 10:37
 */
public class TestA {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(TestA.class);

        try {

            LoadCacheHelper loadCacheA = new LoadCacheHelper(1881, "192.168.199.1");
            TimeUnit.SECONDS.sleep(5);
            ServerHashRing instance = LoadCacheHelper.getServerHashRing();
            System.out.println(instance);
            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("error", e);
        } finally {
            System.exit(0);
        }
    }

}
