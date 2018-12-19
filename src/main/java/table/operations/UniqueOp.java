
package table.operations;

import java.util.HashSet;

/**
 * The UniqueOp class is an operation for the MyTable.runOp() function. It 
 * creates a new HashSet with the unique elements found in the specified column.
 * The getResult() method of the UniqueOp class returns value returns a
 * HashSet object.
 * @author Saud Fatayerji
 */
public class UniqueOp implements TableOperationInterface {
    
    private HashSet<String> set = new HashSet<>();
    
    private final String colTitle;
    private final int colIdx;
    
    /**
     * 
     * @param colTitle
     * @param colIdx 
     */
    public UniqueOp(String colTitle, int colIdx) {
        this.colTitle = colTitle;
        this.colIdx = colIdx;
    }
    
    @Override
    public String getColTitle() {
        return colTitle;
    }
    
    @Override
    public HashSet<String> getResult() {
        return set;
    }
    
    @Override
    public void runOp(String key, String[] row) {
        set.add(row[colIdx]);
    }
    
    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            UniqueOp o = ((UniqueOp) op[i]);
            set.addAll(o.set);
        }
    }
    
    @Override
    public UniqueOp copy() {
        UniqueOp out = new UniqueOp(colTitle, colIdx);
        out.set = new HashSet<>(set);
        return out;
    }
    
}
