//package filter.load;
//
//import filter.load.model.ServerNode;
//import filter.load.model.ServerHashRange;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @ClassName : All
// * @Description :
// * @Author :
// * @Date: 2020-06-30 11:36
// */
//public class All {
//    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(All.class);
//
//     class RouteHelper1 {//响应callBack
//        private List<ServerNode> sortedHashRing;
//
//        public Node  HashRingNode routeServer(int key,List<ServerNode> sortedHashRing) {
//            return null;
//        }
//        public void loadRingHash(){}
//    }
//    //service
//    class FilterTClient{//响应callBack
//        private Map poll;
//
//        public Set<Integer> findNotExit(int userId, Set<Integer> userIds){};
//        public Set<Integer> findExit(int userId, Set<Integer> userIds){};
//
//    }
//    //service
//    class FilterTHandler{
//
//        private FilterService filterService;
//
//        public Set<Integer> findExist(int userId, Set<Integer> userIds) {
//
//            return null;
//        }
//        public Set<Integer> findNotExist(int userId, Set<Integer> userIds) {
//            return null;
//        }
//
//
//
//    }
//    //service
//    class FilterService{
//
//        private BitMap bitMap;
//        //DB
//        //Redis
//        public Set filter(int key,Set<Integer> userIds){};
//
//        private Set filterFromRedis(int key,Set<Integer> userIds){};
//        private Set filterFromBitMap(int key,Set<Integer> userIds){};
//        private Set filterFromDB(int key,Set<Integer> userIds){};
//
//    }
//
//
//    //data
//    class BitMap{
//
//        private final Map bitMap;
//        public void loadBItMap(){};
//        public void loadBItMap(List old,List newl){};
//
//        //jian
//    }
//
//    class zk{
//         //... register callBack
//    }
//
//
//
//
//}
