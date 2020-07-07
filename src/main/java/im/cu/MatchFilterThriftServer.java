package im.cu;


import im.cu.framework.helper.CUBeanFactory;
import im.cu.thrift.client.Nope31DaysCacheThriftClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

        Nope31DaysCacheThriftClient client = CUBeanFactory.getBean(Nope31DaysCacheThriftClient.class);


        Scanner sc = new Scanner(System.in);
        while (true){

            String input = sc.next();
            // uid|id,id,id
            String[] parts = StringUtils.split(input, "|");
            int uid = NumberUtils.toInt(parts[0]);
            String[] idStrList = StringUtils.split(parts[1], ",");
            List<Integer> set = new ArrayList<>();
            for(String str : idStrList){
                set.add(NumberUtils.toInt(str));
            }
            Set<Integer> notExit = client.findExists(uid, set);
            System.out.println(notExit);
        }


    }

}