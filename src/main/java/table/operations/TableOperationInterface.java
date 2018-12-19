
package table.operations;

/**
 * This interface allows users to create new operations for the MyTable.runOp
 * function.
 * @author Saud Fatayerji
 */
public interface TableOperationInterface {
    
    /**
     * Gets the title of the column that this interface operates on.
     * @return The title of the column that this interface operates on.
     */
    public String getColTitle();
    
    /**
     * Gets the result of this operation. The results of the returned
     * operation should be calculated using the runOp() method before calling
     * this method.
     * @return 
     */
    public Object getResult();
    
    /**
     * Runs the operation on the given key/row pair.
     * @param key The primary key of the current row.
     * @param row The content of the current row.
     */
    public void runOp(String key, String[] row);
    
    /**
     * Combine the results generated into this operation. Used when
     * multiprocessing.
     * @param op 
     */
    public void mergeResultsIntoThis(TableOperationInterface... op);
    
    /**
     * Create a deep copy of this operation.
     * @return The deep clone of this operation.
     */
    public Object copy();
    
    /**
     * Clones an array 
     * @param ops
     * @return 
     */
    public static TableOperationInterface[] copyOpsArr(TableOperationInterface... ops) {
        TableOperationInterface[] out = new TableOperationInterface[ops.length];
        for(int i=0; i<out.length; i++) {
            out[i] = (TableOperationInterface) ops[i].copy();
        }
        return out;
    }
    
    /**
     * Gets the results of all the provided ops.
     * @param ops All of the ops to get the results for.
     * @return An array of results obtained from the provided ops.
     */
    public static Object[] getOpsArrResults(TableOperationInterface... ops) {
        Object[] out = new Object[ops.length];
        for(int i=0; i<out.length; i++) {
            out[i] = ops[i].getResult();
        }
        return out;
    }
    
    /**
     * Gets the results of all the provided ops, and converts them to a string.
     * @param ops All of the ops to get the results for.
     * @return An array of results (converted to strings) obtained from the provided ops.
     */
    public static String[] getOpsResultsAsStr(TableOperationInterface... ops) {
        String[] out = new String[ops.length];
        for(int i=0; i<out.length; i++) {
            out[i] = ops[i].getResult().toString();
        }
        return out;
    }
    
}
