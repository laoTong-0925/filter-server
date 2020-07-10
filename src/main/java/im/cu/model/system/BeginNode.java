package im.cu.model.system;


import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : MatchFilterThriftServer
 * @Description : 启动类
 * @Author : t_t
 * @Date: 2020-07-02 14:40
 */
public class BeginNode {
    public static List<String> beginNode = new ArrayList<>();

    static {
        beginNode.add("192.168.0.113:1101");
        beginNode.add("192.168.0.113:1102");
    }

}