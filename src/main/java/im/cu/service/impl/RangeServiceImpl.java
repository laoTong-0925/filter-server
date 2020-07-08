package im.cu.service.impl;

import im.cu.BeginNode;
import im.cu.helper.HashRing.HashRingHelper;
import im.cu.model.LocalServer;
import im.cu.model.ServerHashRange;
import im.cu.model.ServerNode;
import im.cu.service.CacheService;
import im.cu.zk.ZKConfigKey;
import im.cu.zk.ZKFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : LoadCacheHelper
 * @Description :
 * @Author :
 * @Date: 2020-06-20 00:37
 */
@Service
public class RangeServiceImpl implements CacheService {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RangeServiceImpl.class);

    /**
     * 服务区间
     */
    private List<ServerHashRange> serverHashRangeList;

    /**
     * 加载 服务区间
     */
    @Override
    public void loadHashRange() {
        Map<String, String> beginNode;
        if (LocalServer.getIsNew()) {
            String ip = LocalServer.getIp();
            logger.info("-----------新增服务---{}------", ip);
            beginNode = ZKFactory.getAllNode(ZKConfigKey.filterServerNopePath);
            beginNode.put(ip, ip);
        } else {
            beginNode = BeginNode.beginNode;
        }
        //哈希环
        System.out.println("-------loadServerRange------");
        if (beginNode == null)
            return;
        //本服务hash集合
        List<Integer> thisServerForHashRing = new ArrayList<>();
        //哈希环
        List<ServerNode> sortedHashRingList = HashRingHelper.reloadHashRing(beginNode, thisServerForHashRing);
        //区间
        serverHashRangeList = HashRingHelper.initRange(sortedHashRingList, thisServerForHashRing);
        int i = serverHashRangeList.stream().mapToInt(e -> (e.getServerHash() - e.getBeforeServerHash())).sum();
        System.out.println("覆盖范围 ：" + (double) i / (double) Integer.MAX_VALUE);
        logger.info("--------服务区间加载完毕--------");
        serverHashRangeList.forEach(e -> logger.info(e.toString()));
    }

    /**
     * 判断是否需要加载
     *
     * @param userId 用户Id
     * @return boolean
     */
    public boolean isLoad(int userId) {
        if (serverHashRangeList == null)
            return false;
        return HashRingHelper.isLoad(userId, serverHashRangeList);
    }

}
