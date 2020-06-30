package filter.load.model;

/**
 * @ClassName : LocalServer
 * @Description :
 * @Author :
 * @Date: 2020-06-30 17:47
 */
public class LocalServer {

    private static int port;
    private static String ip;

    public LocalServer(int port, String ip) {
        LocalServer.port = port;
        LocalServer.ip = ip;
    }

    public static String getUrl() {
        return ip + ":" + port;
    }

}
