namespace java thrift.gen

service MatchFilterThriftService {

    set<i32> findExist(1:i32 userId, 2:set<i32> userIds);

    set<i32> findNotExist(1:i32 userId, 2:set<i32> userIds);


}