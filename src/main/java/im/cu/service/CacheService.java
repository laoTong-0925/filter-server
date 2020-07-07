package im.cu.service;


import im.cu.match.recent.relation.cache.bitmap.SyncRoaringBitmap;

import java.util.concurrent.ConcurrentHashMap;

public interface CacheService {

    ConcurrentHashMap<Integer, SyncRoaringBitmap> loadToBitMap();

}
