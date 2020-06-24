package test.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.load.hash.HashRing.HashRingHelper;
import test.load.model.HashRingNode;
import test.load.zk.ZKConfigKey;

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

        Logger logger = LoggerFactory.getLogger(TestB.class);

        try {

            LoadCache loadCacheB = new LoadCache(1882, "192.168.199.2");

            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("error", e);
        } finally {
            System.exit(0);
        }
    }
}
