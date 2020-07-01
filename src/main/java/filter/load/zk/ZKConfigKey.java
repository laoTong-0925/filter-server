package filter.load.zk;

/**
 * @ClassName : ZKPathKey
 * @Description :
 * @Author :
 * @Date: 2020-06-20 23:19
 */
public interface ZKConfigKey {

    String filterServer = "FilterServer";

    String filterServerPath = "/filter";

    String filterClientPool = "-ClientPool";

    String filterServerHashRing = "-HashRing";

    int VIRTUAL_NODE_SIZE = 2;

}
