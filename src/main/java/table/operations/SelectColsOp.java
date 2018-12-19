
package table.operations;

import databasepackage.MyTable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The SelectColsOp class is an operation for the MyTable.runOp() function. It 
 * creates a new table with only the specified columns. The getResult() method
 * of the SelectColsOp class returns value returns a MyTable object.
 * @author Saud Fatayerji
 */
public class SelectColsOp implements TableOperationInterface {
    
    private HashMap<String, String[]> map = new HashMap<>();
    
    private final String newTableName;
    private final String[] colTitles;
    private final int[] colIdxs;
    
    /**
     * Creates an SelectColsOp object.
     * @param newTableName The name for the new table.
     * @param colTitles The titles to select.
     * @param colIdxs The indexes of the titles to select.
     */
    public SelectColsOp(String newTableName, String[] colTitles, int[] colIdxs) {
        this.newTableName = newTableName;
        this.colTitles = colTitles;
        this.colIdxs = colIdxs;
    }

    @Override
    public String getColTitle() {
        return null;
    }

    @Override
    public MyTable getResult() {
        MyTable table = new MyTable(newTableName, colTitles);
        table.getRows().putAll(map);
        return table;
    }

    @Override
    public void runOp(String key, String[] row) {
        String[] newRow = new String[colIdxs.length];
        for(int i=0; i<colIdxs.length; i++) {
            newRow[i] = row[colIdxs[i]];
        }
        map.put(key, newRow);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            SelectColsOp o = ((SelectColsOp) op[i]);
            map.putAll(o.map);
        }
    }

    @Override
    public SelectColsOp copy() {
        SelectColsOp out = new SelectColsOp(newTableName, colTitles, colIdxs);
        for(Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            out.map.put(key, Arrays.copyOf(value, value.length));
        }
        return out;
    }
    
}
