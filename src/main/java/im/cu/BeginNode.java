package im.cu;


import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : MatchFilterThriftServer
 * @Description : 启动类
 * @Author : t_t
 * @Date: 2020-07-02 14:40
 */
public class BeginNode {
    public static Map<String, String> beginNode = new HashMap<>();

    static {
        beginNode.put("127.0.0.1:1101", "127.0.0.1:1101");
        beginNode.put("127.0.0.1:1102", "127.0.0.1:1102");
    }

}