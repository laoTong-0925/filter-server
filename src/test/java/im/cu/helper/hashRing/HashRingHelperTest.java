package im.cu.helper.hashRing;


import im.cu.model.ServerHashRange;
import im.cu.model.ServerNode;
import im.cu.model.system.LocalServer;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HashRingHelperTest {


    @Test
    public void initRange() {
    }

    @Test
    public void reloadHashRing() {
        List<String> realNodeList = new ArrayList<>();
        realNodeList.add("127.0.0.1:1101");
        realNodeList.add("127.0.0.1:1102");
        realNodeList.add("127.0.0.1:1103");
        realNodeList.add("127.0.0.1:1104");

        new LocalServer("127.0.0.1:1104");
        List<Integer> thisServerForHashRing = new ArrayList<>();
        List<ServerNode> sortedHashRing = HashRingHelper.reloadHashRing(realNodeList, thisServerForHashRing);
        if (CollectionUtils.isNotEmpty(sortedHashRing))
            sortedHashRing.forEach(System.out::println);
        System.out.println();
        List<ServerHashRange> serverHashRanges = HashRingHelper.initRange(sortedHashRing, thisServerForHashRing);
        if (CollectionUtils.isNotEmpty(serverHashRanges))
            serverHashRanges.forEach(System.out::println);
    }

}
