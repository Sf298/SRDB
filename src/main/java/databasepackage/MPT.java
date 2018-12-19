
package databasepackage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides a set of tools that can be used to simplify the implementation of
 * Muti-Processing Systems
 * @author Saud Fatayerji
 */
public class MPT {
    
    /**
     * Gets the range of elements to assign to an individual processor/thread.
     * @param proc The index of the current processor
     * @param nprocs The number of processors available.
     * @param maxI Number of elements to partition.
     * @return An array of the left (inclusive) and right (exclusive) indexes respectively.
     */
    public static int[] getLR(int proc, int nprocs, int maxI) {
        // calc ileft and iright
        int a = maxI/nprocs; // cols per process
        int rem = maxI%nprocs; // remaining cols
        int left = proc * a + ((proc<rem) ? proc : rem); // is 0 based
        int right = left + a + ((proc<rem) ? 1 : 0); // right is exclusive
        return new int[] {left, right};
    }
    
    /**
     * Converts the left and right indexes into an array of consecutive values in
     * the range of the values provided.
     * @param lr The LR array as returned by getLR()
     * @return an array with values in the range of the LR values
     */
    public static int[] lr2Range(int[] lr) {
        return lr2Range(lr[0], lr[1]);
    }
    
    /**
     * Converts the left and right indexes into an array of consecutive values in
     * the range of the values provided.
     * @param left The starting value in the range (inclusive)
     * @param right The end value in the range (exclusive)
     * @return an array with values in the range of the LR values
     */
    public static int[] lr2Range(int left, int right) {
        int[] out = new int[right-left];
        for(int i=0; i<out.length; i++) {
            out[i] = left+i;
        }
        return out;
    }
    
    /**
     * Evenly distributes the values provided in the ArrayList into nprocs
     * ArrayLists.
     * @param nprocs The number of processors for which to split the list.
     * @param list The list of values which should be split.
     * @return An array of ArrayLists containing all the values that should be
     * processed by the respective processor.
     */
    public static <T> ArrayList<T>[] split(int nprocs, ArrayList<T> list) {
        ArrayList<T>[] out = new ArrayList[nprocs];
        for(int i=0; i<nprocs; i++) {
            int[] lr = getLR(i, nprocs, list.size());
            out[i] = new ArrayList<>(list.subList(lr[0], lr[1]));
        }
        return out;
    }
    
    /**
     * Evenly distributes the values provided in the HashMap into nprocs
     * HashMaps.
     * @param nprocs The number of processors for which to split the list.
     * @param map The map of values who's contents will be split.
     * @return An array of HashMaps containing all the key-value pairs that
     * should be processed by the respective processor.
     */
    public static <K,V> HashMap<K,V>[] split(int nprocs, HashMap<K,V> map) {
        ArrayList<K>[] keys = split(nprocs, new ArrayList<>(map.keySet()));
        HashMap<K,V>[] out = new HashMap[10];
        for(int i=0; i<keys.length; i++) {//ArrayList<K> kList : keys) {
            HashMap<K,V> m = new HashMap<>();
            for(K k : keys[i]) {
                m.put(k, map.get(k));
            }
            out[i] = m;
        }
        return out;
    }
    
}
