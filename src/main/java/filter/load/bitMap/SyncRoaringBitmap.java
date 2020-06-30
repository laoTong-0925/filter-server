package filter.load.bitMap;

import org.roaringbitmap.IntConsumer;
import org.roaringbitmap.RoaringBitmap;

import java.util.Iterator;
import java.util.List;

/**
 * * 线程安全
 * * 修改时间维护
 *
 * @Author : t_t
 * @Date: 2020-06-30 13:19
 */
public class SyncRoaringBitmap {
    private volatile long magic;
    private final RoaringBitmap bitmap;

    private long lastModifyTimeMillis;

    public SyncRoaringBitmap() {
        this.bitmap = new RoaringBitmap();
    }

    public SyncRoaringBitmap(RoaringBitmap bitmap) {
        this.bitmap = bitmap;
    }

    // **************************************************
    //               update operation
    // **************************************************

    public boolean checkedAdd(int x) {
        synchronized (bitmap) {
            boolean b = bitmap.checkedAdd(x);
            if (b) {
                lastModifyTimeMillis = System.currentTimeMillis();
                magic++;
            }
            return b;
        }
    }

    public boolean checkedRemove(int x) {
        synchronized (bitmap) {
            boolean b = bitmap.checkedRemove(x);
            if (b) {
                lastModifyTimeMillis = System.currentTimeMillis();
                magic--;
            }
            return b;
        }
    }

    public void batchAppend(List<Integer> userIds) {
        synchronized (bitmap) {
            userIds.forEach(bitmap::add);
        }
    }

    // **************************************************
    //               read operation
    // **************************************************


    public RoaringBitmap getBitmap() {
        if (magic != 0) {
            return bitmap;
        }
        return bitmap;
    }

    public long getLastModifyTimeMillis() {
        if (magic != 0) {
            return lastModifyTimeMillis;
        }
        return lastModifyTimeMillis;
    }

    public boolean contains(int x) {
        // read-volatile
        if (magic != 0) {
            synchronized (bitmap) {
                return bitmap.contains(x);
            }
        }
        return false;
    }

    public int getCardinality() {
        // read-volatile
        if (magic != 0) {
            synchronized (bitmap) {
                return bitmap.getCardinality();
            }
        }
        return 0;
    }

    public void forEach(IntConsumer ic) {
        // read-volatile
        if (magic >= 0) {
            synchronized (bitmap) {
                bitmap.forEach(ic);
            }
        }
    }

    public boolean isEmpty() {
        // read-volatile
        if (magic >= 0) {
            synchronized (bitmap) {
                return bitmap.isEmpty();
            }
        }
        return false;
    }

    public Iterator<Integer> iterator() {
        // read-volatile
        if (magic >= 0) {
            return bitmap.iterator();
        }
        return bitmap.iterator();
    }
}
