package im.cu;

import com.wealoha.common.config.Config;
import im.cu.base.constants.ConfigStringListKeys;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : D
 * @Description :
 * @Author :
 * @Date: 2020-07-10 10:37
 */
public class D {

    public static void main(String[] args) {

        List<String> list;
        for (int i = 0; i < 500; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                list = Config.instance.get(ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2);
                System.out.println(list.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
