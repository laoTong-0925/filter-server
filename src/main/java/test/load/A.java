//package test.load;
//
//import java.util.List;
//
///**
// * @author hongweixu
// * @since 2020-06-19 16:28
// */
//public class A {
//
//
//    interface ZkHelper {
//        void set();
//
//        String get(String key);
//
//        void onChange(String newData);
//    }
//
//
//    abstract class HashRing<K, T> {
//        // hashStrategy
//
//        private List<T> serverNodeList;
//
//        public abstract T get(K key);
//
//        public abstract HashRing reloadHashRing(List<T> serverList);
//    }
//
//
//    class CacheServer<K, T> {
//        private HashRing ring;// 自动reload
//        private zk;
//
//        public T get(K key);
//
//    }
//
//    public static void main(String[] args) {
//
//        // 第一次构建服务的时候
//        serverList = args[0]; //  ["91:10010", "92:10010", "93:10010", "94:10010"]
//
//        hashRing = ["91:10010", "91:10010-A", "91:10010-B", "91:10010-C", "91:10010-D",
//                "92:10010", "91:10010-A", "91:10010-B", "91:10010-C", "91:10010-D",
//                "93:10010", "91:10010-A", "91:10010-B", "91:10010-C", "91:10010-D",
//                "94:10010", "91:10010-A", "91:10010-B", "91:10010-C", "91:10010-D"
//        "95:10010", "91:10010-A", "91:10010-B", "91:10010-C", "91:10010-D"
//        ]
//
//
//        // 启动一个新服务
//
//        T server = getSelf(); //  "95:10010"",
//
//        nodeList = ZkHelper.getAll();
//
//        nodeList.add(server);
//
//        hashRing = HashRing.reloadHashRing(nodeList);
//
//        userIds = userIds.stream().filter(e -> hashRing.get(e) == server).collect(toList);
//
//
//        // 客户端
//        nodeList = ZkHelper.getAll();
//        hashRing = HashRing.reloadHashRing(nodeList);
//        server = hashRing.get(userId);
//        server.invoke();
//
//
//        userIds = [1, 100_0000];
//
//
//
//
//
//
//
//
//        System.exit(0);
//
//    }
//
//}