package filter.load.model;

/**
 * @ClassName : LocalServer
 * @Description :
 * @Author :
 * @Date: 2020-06-30 17:47
 */
public class LocalServer {

    private static String ip;
    private static boolean isNew;

    public static String getIp() {
        return ip;
    }

    public static boolean getIsNew() {
        return isNew;
    }

    public LocalServer(String ip, boolean isNew) {
        LocalServer.ip = ip;
        LocalServer.isNew = isNew;
    }

    public LocalServer(String ip) {
        this(ip, false);
    }
}
