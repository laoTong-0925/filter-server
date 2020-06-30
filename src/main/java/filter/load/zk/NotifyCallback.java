package filter.load.zk;

/**
 * 某个路径变更的回调
 *
 * @author javamonk
 * @createTime 2020年5月3日 下午8:34:06
 */
public interface NotifyCallback {

    /**
     * 结点变更
     *
     * @param path
     * @param oldData
     * @param newData
     */
    public void onChange(String path, String oldData, String newData);
}