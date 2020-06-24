package test.load;

import test.load.model.HashRingNode;

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
            LoadCache loadCacheC = new LoadCache(1883, "192.168.199.3");
            Integer[] users = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1001};
            for (int i = 0; i < users.length - 1; i++) {
                LoadCache.isLoad(users[i]);
            }
            TimeUnit.SECONDS.sleep(2);
            HashRingNode server = LoadCache.getServer(10000);
            if (server != null) {
                System.out.println(server);
            }
//            HashRingNode server2 = LoadCache.getServer(Integer.MAX_VALUE);
//            System.out.println(server.toString());
//            System.out.println(server2.toString());
            TimeUnit.MINUTES.sleep(5);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
