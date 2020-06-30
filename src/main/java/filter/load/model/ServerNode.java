package filter.load.model;

/**
 * @ClassName : HashRingNode
 * @Description : 哈希环上的结点
 * @Author : t_t
 * @Date: 2020-06-20 22:37
 */
public class ServerNode {

    private final int hash;

    private final String url;

    public String getUrl() {
        return url;
    }

    public Integer getHash() {
        return hash;
    }

    public ServerNode(Integer hash, String url) {
        this.hash = hash;
        this.url = url;
    }

    @Override
    public String toString() {
        return "HashRingNode{" +
                "hash=" + hash +
                ", url='" + url + '\'' +
                '}';
    }
}
