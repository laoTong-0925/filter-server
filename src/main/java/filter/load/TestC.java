package filter.load;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName : TestC
 * @Description :
 * @Author :
 * @Date: 2020-06-24 14:49
 */
public class TestC {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestC.class);

    public static void main(String[] args) {

        try {
            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
