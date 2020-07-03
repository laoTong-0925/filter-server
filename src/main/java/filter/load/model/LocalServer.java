package filter.load.model;

/**
 * @ClassName : LocalServer
 * @Description :
 * @Author :
 * @Date: 2020-06-30 17:47
 */
public class LocalServer {

    private static String ip;

    public static String getIp() {
        return ip;
    }

    public LocalServer(String ip) {
        LocalServer.ip = ip;
    }
}
