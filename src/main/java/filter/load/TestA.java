package filter.load;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * @ClassName : TestA
 * @Description :
 * @Author :
 * @Date: 2020-06-20 10:37
 */
public class TestA {


    public static void main(String[] args) throws Exception {
        InetAddress ip4 = Inet4Address.getLocalHost();
        System.out.println(ip4.getHostAddress());
    }
}

