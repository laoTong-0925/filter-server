package im.cu.service.impl;

import com.wealoha.common.config.Config;
import im.cu.base.constants.ConfigStringListKeys;
import im.cu.helper.hashRing.HashRingHelper;
import im.cu.model.ServerHashRange;
import im.cu.model.ServerNode;
import im.cu.model.system.LocalServer;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : LoadCacheHelper
 * @Description : 负责加载服务区间 用于数据加载或清洗
 * @Author :
 * @Date: 2020-06-20 00:37
 */
@Service
public class RangeService {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RangeService.class);

    /**
     * 服务区间
     */
    private static List<ServerHashRange> serverHashRangeList;

    @Autowired
    private Nope31DaysCacheService nope31DaysCacheService;

    /**
     * 加载 服务区间
     */
    public void loadHashRange(boolean isCallBack) {
        List<String> beginNode;
        if (CollectionUtils.isEmpty(LocalServer.getBeginNode()) || isCallBack) {
            String ip = LocalServer.getUrl();
            beginNode = Config.instance.get(ConfigStringListKeys.ThriftMutableNope31DaysCacheServerV2);
            if (!isCallBack) {
                logger.info("-----------新增服务---{}------", ip);
                beginNode.add(ip);
            }
        } else {
            beginNode = LocalServer.getBeginNode();
        }
        //哈希环
        logger.info("-------loadServerRange------");
        if (beginNode == null)
            return;
        //本服务hash集合
        List<Integer> thisServerForHashRing = new ArrayList<>();
        //哈希环
        List<ServerNode> sortedHashRingList = HashRingHelper.reloadHashRing(beginNode, thisServerForHashRing);
        //区间
        serverHashRangeList = HashRingHelper.initRange(sortedHashRingList, thisServerForHashRing);
        int i = serverHashRangeList.stream().mapToInt(e -> (e.getServerHash() - e.getBeforeServerHash())).sum();
        logger.info("覆盖范围 ：" + (double) i / (double) Integer.MAX_VALUE);
        logger.info("--------服务区间加载完毕--------");
        serverHashRangeList.forEach(e -> logger.info(e.toString()));
        if (isCallBack) {
            nope31DaysCacheService.reload();
        }
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
