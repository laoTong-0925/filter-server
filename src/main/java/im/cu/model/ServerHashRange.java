package im.cu.model;

public class ServerHashRange {
    private final Integer serverHash;
    private final Integer beforeServerHash;
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


    public ServerHashRange(Integer serverHash, Integer beforeServerHash, Boolean isLast) {
        this.serverHash = serverHash;
        this.beforeServerHash = beforeServerHash;
        this.isLast = isLast;
    }

    public ServerHashRange(Integer serverHash, Integer beforeServerHash) {
        this(serverHash, beforeServerHash, false);
}

    @Override
    public String toString() {
        return "ServerHashRange{" +
                "serverHash=" + serverHash +
                ", beforeServerHash=" + beforeServerHash +
                ", isLast=" + isLast +
                '}';
    }
}