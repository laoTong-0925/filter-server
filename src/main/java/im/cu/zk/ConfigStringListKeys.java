package im.cu.zk;

import com.wealoha.common.codec.JSONCodec;
import com.wealoha.common.config.Config.ConfigKey;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author javamonk
 * @createTime 2014年5月1日 下午12:04:33
 */
public enum ConfigStringListKeys implements ConfigKey<List<String>> {

    /**
     * 过滤服务
     */
    // 近期31天全量数据cache，自动加载最新一天数据
    ThriftMutableNope31DaysCacheServer("thriftMutableNope31DaysCacheServer"),
    ThriftMatchFilterServer("thriftMatchFilterServer");

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String name;

    private List<String> defaultValues;

    private static Map<String, ConfigStringListKeys> namesMap;

    static {
        namesMap = new HashMap<>();
        for (ConfigStringListKeys t : values()) {
            ConfigStringListKeys exist = namesMap.put(t.name(), t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        namesMap = Collections.unmodifiableMap(namesMap);
    }

    ConfigStringListKeys(String name) {
        this.name = name;
    }

    ConfigStringListKeys(String name, List<String> defaultValues) {
        this.name = name;
        this.defaultValues = defaultValues;
    }

    @Override
    public String configKey() {
        return name;
    }

    @Override
    public List<String> defaultValue() {
        if (CollectionUtils.isNotEmpty(defaultValues)) {
            return defaultValues;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> value(String rawValue) {
        try {
            return JSONCodec.decode(rawValue, List.class);
        } catch (Exception e) {
            logger.error("parse config json fail: " + rawValue, e);
            return defaultValue();
        }
    }

    @Override
    public String encode(List<String> value) {
        return JSONCodec.encode(value);
    }


    public static ConfigStringListKeys fromName(String name) {
        return namesMap.get(name);
    }
}
