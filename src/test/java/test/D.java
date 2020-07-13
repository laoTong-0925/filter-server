package test;

import com.wealoha.common.config.Config;
import com.wealoha.thrift.ServiceInfo;
import im.cu.base.constants.ConfigStringListKeys;
import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void addAll() {

        try {
            // TODO
            List<ServiceInfo> servicesList = new ArrayList<>();
            List<ServiceInfo> s = new ArrayList<>();
            s.add(new ServiceInfo("1", 1));

            servicesList.add(new ServiceInfo("1", 1));
            servicesList.add(new ServiceInfo("2", 2));
            servicesList.add(new ServiceInfo("3", 3));
            servicesList.removeAll(s);
            System.out.println(servicesList);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
