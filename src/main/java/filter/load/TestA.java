package filter.load;

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

            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("error", e);
        } finally {
            System.exit(0);
        }
    }

}
