package im.cu;


import im.cu.framework.helper.CUBeanFactory;
import im.cu.thrift.client.Nope31DaysCacheThriftClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * @ClassName : ClientRunner
 * @Description : 客户端
 * @Author :
 * @Date: 2020-07-03 15:27
 */
public class ClientRunner {

    public static void main(String[] args) {
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
