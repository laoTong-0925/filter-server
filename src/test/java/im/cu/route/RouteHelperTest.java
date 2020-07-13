package im.cu.route;


import im.cu.hash.FnvHashStrategy;
import im.cu.model.ServerNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RouteHelperTest {

    @Test
    public void routeTest() {

        FnvHashStrategy hash = new FnvHashStrategy();

        List<ServerNode> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new ServerNode(hash.getHashCode("127.101" + i), "127.101"));
        }
        for (int i = 0; i < 3; i++) {
            list.add(new ServerNode(hash.getHashCode("127.102" + i), "127.102"));
        }
        for (int i = 0; i < 3; i++) {
            list.add(new ServerNode(hash.getHashCode("127.103" + i), "127.103"));
        }
        List<ServerNode> collect = list.stream()
                .sorted(Comparator.comparing(ServerNode::getHash))
                .collect(Collectors.toList());
        collect.forEach(e -> System.out.println(e.getHash()));
        for (int i = 1; i <= 100; i++) {
            RouteHelper.routeServer(i, collect);
        }
    }

}
