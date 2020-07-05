package filter.load.zk;

/**
 * 某个路径变更的回调
 *
 * @author t_t
 * @createTime 2020年5月3日 下午8:34:06
 */
public interface NotifyCallback {

    /**
     * 结点通知
     */
    public void onChange();
}