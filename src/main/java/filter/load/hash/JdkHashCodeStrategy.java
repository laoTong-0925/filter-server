package filter.load.hash;

/**
 * @author daofeng.xjf
 * @date 2019/2/15
 */
public class JdkHashCodeStrategy implements HashStrategy {

    @Override
    public int getHashCode(String origin) {
        return Math.abs(origin.hashCode());
    }

}
