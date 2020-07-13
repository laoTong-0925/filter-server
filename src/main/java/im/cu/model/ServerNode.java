package im.cu.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

    public int getHash() {
        return hash;
    }

    public ServerNode(int hash, String url) {
        this.hash = hash;
        this.url = url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
