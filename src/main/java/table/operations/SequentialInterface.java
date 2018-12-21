
package table.operations;

/**
 * This interface allows users to create new operations for the
 * MyTable.runOpsSequentially() method.
 * @author Saud Fatayerji
 */
public interface SequentialInterface extends TableOperationInterface {
    
    /**
     * Make the required changes to the provided row.
     * @param key The key for the current row.
     * @param row The content of the current row.
     * @return The new row to add to the table. If null, no row is added.
     */
    public String[] processRow(String key, String[] row);
    
    /**
     * Get the new column titles for the table that this operation normally
     * creates.
     * @return The column titles.
     */
    public String[] getNewTitles();
    
    /**
     * Clones an array 
     * @param ops The operations to copy.
     * @return A deep copy of the provided operations.
     */
    public static SequentialInterface[] copyOpsArr(SequentialInterface... ops) {
        SequentialInterface[] out = new SequentialInterface[ops.length];
        for(int i=0; i<out.length; i++) {
            out[i] = (SequentialInterface) ops[i].copy();
        }
        return out;
    }
    
}
