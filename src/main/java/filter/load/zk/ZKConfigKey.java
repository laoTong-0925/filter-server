package filter.load.zk;

/**
 * @ClassName : ZKPathKey
 * @Description :
 * @Author :
 * @Date: 2020-06-20 23:19
 */
public interface ZKConfigKey {

    String filterServerPath = "/filter";

    String filterServerNode = "-filterServerHash";

    Integer VIRTUAL_NODE_SIZE = 2;

}
