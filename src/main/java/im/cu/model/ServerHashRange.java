package im.cu.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @ClassName : ServerHashRange
 * @Description : 服务自身的哈希环区间
 * @Author : t_t
 * @Date: 2020-06-30 17:47
 */
public class ServerHashRange {
    private final int serverHash;
    private final int beforeServerHash;
    private final boolean isLast;

    public int getServerHash() {
        return serverHash;
    }

    public int getBeforeServerHash() {
        return beforeServerHash;
    }

    public boolean getLast() {
        return isLast;
    }


    public ServerHashRange(int serverHash, int beforeServerHash, boolean isLast) {
        this.serverHash = serverHash;
        this.beforeServerHash = beforeServerHash;
        this.isLast = isLast;
    }

    public ServerHashRange(int serverHash, int beforeServerHash) {
        this(serverHash, beforeServerHash, false);
}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}