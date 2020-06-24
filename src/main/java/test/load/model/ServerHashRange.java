package test.load.model;

public class ServerHashRange {
    private final Integer serverHash;
    private final Integer beforeServerHash;
    private final Integer headServerHash;
    private final Boolean isLast;

    public Integer getServerHash() {
        return serverHash;
    }

    public Integer getBeforeServerHash() {
        return beforeServerHash;
    }

    public Boolean getLast() {
        return isLast;
    }

    public Integer getHeadServerHash() {
        return headServerHash;
    }

    public ServerHashRange(Integer serverHash, Integer beforeServerHash, Boolean isLast, Integer headServerHash) {
        this.serverHash = serverHash;
        this.beforeServerHash = beforeServerHash;
        this.isLast = isLast;
        this.headServerHash = headServerHash;
    }

    public ServerHashRange(Integer serverHash, Integer beforeServerHash) {
        this(serverHash, beforeServerHash, false, null);
    }

    @Override
    public String toString() {
        return "ServerHashRange{" +
                "serverHash=" + serverHash +
                ", beforeServerHash=" + beforeServerHash +
                ", headServerHash=" + headServerHash +
                ", isLast=" + isLast +
                '}';
    }
}