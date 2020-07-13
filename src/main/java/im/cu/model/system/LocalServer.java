package im.cu.model.system;

import java.util.List;

/**
 * @ClassName : LocalServer
 * @Description :
 * @Author :
 * @Date: 2020-06-30 17:47
 */
public class LocalServer {

    private static String url;
    public static List<String> beginNode;

    public static String getUrl() {
        return url;
    }

    public static List<String> getBeginNode() {
        return beginNode;
    }

    public LocalServer(String url, List<String> beginNode) {
        LocalServer.url = url;
        LocalServer.beginNode = beginNode;
    }

    public LocalServer(String ip) {
        this(ip, null);
    }
}
