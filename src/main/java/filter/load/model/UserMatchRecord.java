package filter.load.model;

import com.wealoha.common.entity.AbstractDataAttributeEntity;
import com.wealoha.common.entity.DataAttributeKey;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * {@link #getUserRank()}, {@link #getToUserRank()},
 * {@link #getAlohaSource()} 几个属性，只有在从db中取出(使用迭代器)用于统计时才有效
 *
 * @author javamonk
 * @createTime 2014年7月17日 下午6:24:32
 */
public class UserMatchRecord extends AbstractDataAttributeEntity {

    public static final DataAttributeKey<Integer> KEY_USER_RANK = new DataAttributeKey<>(UserMatchRecord.class, "ur", Integer.class);

    public static final DataAttributeKey<Integer> KEY_TO_USER_RANK = new DataAttributeKey<>(UserMatchRecord.class, "tr", Integer.class);

    public static final DataAttributeKey<Integer> KEY_ALOHA_SOURCE = new DataAttributeKey<>(UserMatchRecord.class, "as", Integer.class);
    public static final DataAttributeKey<String> KEY_NOPE_DAILY_SAVED = new DataAttributeKey<>(UserMatchRecord.class, "nd", String.class);

    private final long id;

    private final int userId;

    private final int toUserId;

    private final boolean liked;

    private final Date createTime;

    private final String data;

    public UserMatchRecord(long id, int userId, int toUserId, boolean liked, Date createTime) {
        super();
        this.id = id;
        this.userId = userId;
        this.toUserId = toUserId;
        this.liked = liked;
        this.createTime = createTime;
        this.data = null;
    }

    public UserMatchRecord(long id, int userId, int toUserId, boolean liked, Date createTime,
                           String data) {
        super();
        this.id = id;
        this.userId = userId;
        this.toUserId = toUserId;
        this.liked = liked;
        this.createTime = createTime;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public boolean isLiked() {
        return liked;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getData() {
        return data;
    }

//    /**
//     * Aloha/Nope时用户的Rank，半途开始记录，不是所有的表都有
//     * http://redmine.wealoha.com/issues/349
//     *
//     * @return
//     */
//    public AvatarRank getUserRank() {
//        Integer rank = getDataAttr(KEY_USER_RANK).get();
//        if (rank != null) {
//            return AvatarRank.fromValue(rank);
//        }
//        return null;
//    }
//
//    /**
//     * 被Aloha/Nope用户的Rank，半途开始记录，不是所有的表都有
//     * http://redmine.wealoha.com/issues/349
//     *
//     * @return
//     */
//    public AvatarRank getToUserRank() {
//        Integer rank = getDataAttr(KEY_TO_USER_RANK).get();
//        if (rank != null) {
//            return AvatarRank.fromValue(rank);
//        }
//        return null;
//    }
//
//    /**
//     * Aloha来源
//     *
//     * @return
//     */
//    public AlohaSource getAlohaSource() {
//        Integer value = getDataAttr(KEY_ALOHA_SOURCE).get();
//        if (value != null) {
//            return AlohaSource.fromValue(value);
//        }
//        return null;
//    }
//
//    /**
//     * 日表已保存
//     * @return (注意2019-12-03当日有微量数据没有保存日表)
//     */
//    public boolean isNopeDailySaved() {
//        return StringUtils.isNotBlank(getDataAttr(KEY_NOPE_DAILY_SAVED).get());
//    }
//
//    public ClientAttributeHelper.ClientAttributes getClientAttributes() {
//        return ClientAttributeHelper.getClientAttributes(this);
//    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (liked ? 1231 : 1237);
        result = prime * result + toUserId;
        result = prime * result + userId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // 只看user,toUser,liked三个属性
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UserMatchRecord other = (UserMatchRecord) obj;
        if (liked != other.liked) return false;
        if (toUserId != other.toUserId) return false;
        return userId == other.userId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
