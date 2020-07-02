package filter.load.service;

import filter.load.bitMap.SyncRoaringBitmap;

import java.util.Collection;
import java.util.Map;

public interface CacheService {

    Map<Integer, SyncRoaringBitmap> getBitmap();

    void addIntoBitMap(int userId, Collection<Integer> userIds);

    public void loadToBitMap();


}
