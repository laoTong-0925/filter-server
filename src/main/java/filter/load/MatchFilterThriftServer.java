package filter.load;

import filter.load.context.CUBeanFactory;
import filter.load.thrift.client.MatchFilterThriftServiceClient;

import java.util.*;

/**
 * @ClassName : MatchFilterThriftServer
 * @Description : 启动类
 * @Author : t_t
 * @Date: 2020-07-02 14:40
 */
public class MatchFilterThriftServer {
    public static Map<String, String> beginNode = new HashMap<>();
    public static Map<Integer, List<Integer>> dataMap;

    static {
        beginNode.put("127.0.0.1:1103", "127.0.0.1:1103");
        beginNode.put("127.0.0.1:1101", "127.0.0.1:1101");
        beginNode.put("127.0.0.1:1102", "127.0.0.1:1102");
//        beginNode.put("127.0.0.1:1104", "127.0.0.1:1104");
        dataMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(i * 1000 + 2);
            list.add(i * 1000 + 3);
            list.add(i * 1000 + 4);
            dataMap.put(i * 1000 + 1, list);
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        int port = 1100;
//        LocalServer localServer = new LocalServer("127.0.0.1:" + port);
//        CacheService cacheService = CUBeanFactory.getBean(CacheService.class);
//        MatchFilterThriftServiceHandler handler = CUBeanFactory.getBean(MatchFilterThriftServiceHandler.class);
//        cacheService.loadToBitMap();
//        MatchFilterThriftService.Processor<MatchFilterThriftServiceHandler> processor = new MatchFilterThriftService.Processor<>(handler);
//        int size = cacheService.getBitmap().size();
//        System.out.println(size);

        MatchFilterThriftServiceClient client = CUBeanFactory.getBean(MatchFilterThriftServiceClient.class);
        Set<Integer> set = new HashSet<>();
        set.add(111);
        set.add(8002);
        set.add(1);

        Set<Integer> notExit = client.findNotExit(8001, set);
        System.out.println(notExit);


    }

}