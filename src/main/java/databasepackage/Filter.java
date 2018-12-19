
package databasepackage;

/**
 * The Filter interface is intended to provide a highly customisable way of
 * filtering rows using the FilterOp class.
 * @author Saud Fatayerji
 */
public interface Filter<K,V> {
    
    /**
     * Chooses whether or not to include the row in the output Table
     * @param k The primary key of the row.
     * @param v The row content.
     * @return true if the row should be included in the output Table, otherwise
     * false.
     */
    public boolean shouldKeep(K k, V v);
    
}
