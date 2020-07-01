package filter.load;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : TestData
 * @Description :
 * @Author :
 * @Date: 2020-06-24 15:48
 */
public class TestData {
    public static Integer[] users = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900};
    public static Integer[] Aloha = {1, 10};
    public static Map<String, String> beginNode = new HashMap<>();

    static {
        beginNode.put("192.168.1.1:1100", "192.168.1.1:1100");
        beginNode.put("192.168.1.1:1101", "192.168.1.1:1101");
        beginNode.put("192.168.1.1:1102", "192.168.1.1:1102");
    }


}
