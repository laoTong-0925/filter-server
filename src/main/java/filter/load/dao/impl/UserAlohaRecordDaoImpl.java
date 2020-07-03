package filter.load.dao.impl;

import filter.load.dao.UserAlohaRecordDao;
import filter.load.model.UserMatchRecord;
import filter.load.thrift.Base.AlohaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName : UserAlohaRecordDaoImpl
 * @Description :
 * @Author :
 * @Date: 2020-07-02 16:22
 */
@Service
public class UserAlohaRecordDaoImpl implements UserAlohaRecordDao, RowMapper<UserMatchRecord> {

    private static final Logger logger = LoggerFactory.getLogger(UserAlohaRecordDaoImpl.class);

//    private NamedParameterJdbcTemplate jdbcTemplate;

    @PostConstruct
    private void init() {
//        jdbcTemplate = new NamedParameterJdbcTemplate(DataSourceFactory.getDataSource(DBConfigKey.Match2));
//        jdbcTemplate.setCacheLimit(0);


    }

    @Override
    public UserMatchRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        String data;
        data = rs.getString("data");
        return new UserMatchRecord(rs.getLong("id"),
                rs.getInt("user_id"),
                rs.getInt("to_user_id"),
                true,
                new Date(rs.getTimestamp("create_time").getTime()),
                data);
    }

    @Override
    public Set<Integer> filterMatchUsers(int userId, Collection<Integer> targetUserIds) {


        return null;
    }

    @Override
    public List<Integer> findAllByUser(AlohaType type, int userId) {

        return null;
    }
}
