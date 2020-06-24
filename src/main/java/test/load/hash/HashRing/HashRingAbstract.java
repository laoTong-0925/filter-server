//package test.load.hash.HashRing;
//
//import test.load.model.HashRingNode;
//
//import java.util.List;
//
///**
// * @ClassName : HashRing
// * @Description : 哈希环
// * @Author : t_t
// * @Date: 2020-06-20 22:30
// */
//public abstract class HashRingAbstract<T, K> {
//
//    /**
//     * 获取对应key的服务
//     *
//     * @param key
//     * @return
//     */
//    public abstract T get(K key);
//
//    /**
//     * 是否加载此用户
//     *
//     * @param userId
//     * @return
//     */
//    public abstract boolean isLoad(Integer userId);
//
//    /**
//     * 返回排序的服务结点,自己会去 zk 取所有服务
//     *
//     * @return
//     */
//    public abstract void reloadHashRing();
//
//}
