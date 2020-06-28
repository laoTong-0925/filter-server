package filter.load;

import filter.load.client.FilterServerClient;
import filter.load.model.HashRingNode;

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
            LoadCacheHelper loadCacheC = new LoadCacheHelper(1883, "192.168.199.3");
            Integer[] users = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1001};
            for (int i = 0; i < users.length - 1; i++) {
                LoadCacheHelper.isLoad(users[i]);
            }
            TimeUnit.SECONDS.sleep(2);
            HashRingNode server = FilterServerClient.getServer(10000);
            if (server != null) {
                System.out.println(server);
            }
            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
