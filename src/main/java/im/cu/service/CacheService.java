package im.cu.service;


public interface CacheService {

    void loadHashRange();

    boolean isLoad(int userId);
}
