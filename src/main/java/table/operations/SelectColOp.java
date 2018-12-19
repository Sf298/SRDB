
package table.operations;

import java.util.HashMap;
import java.util.Map;

/**
 * The SelectColOp class is an operation for the MyTable.runOp() function. It 
 * creates a new HashMap with only the content of the specified column. The
 * getResult() method of the SelectColOp class returns value returns a HashMap
 * object.
 * @author Saud Fatayerji
 */
public class SelectColOp implements TableOperationInterface {
    
    private HashMap<String, String> map = new HashMap<>();
    
    private final String colTitle;
    private final int colIdx;
    
    /**
     * Creates an SelectColOp object.
     * @param colTitle The title of the column to process.
     * @param colIdx The index of the column to process.
     */
    public SelectColOp(String colTitle, int colIdx) {
        this.colTitle = colTitle;
        this.colIdx = colIdx;
    }

    @Override
    public String getColTitle() {
        return colTitle;
    }

    @Override
    public HashMap<String, String> getResult() {
        return map;
    }

    @Override
    public void runOp(String key, String[] row) {
        map.put(key, row[colIdx]);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            SelectColOp o = ((SelectColOp) op[i]);
            map.putAll(o.map);
        }
    }

    @Override
    public SelectColOp copy() {
        SelectColOp out = new SelectColOp(colTitle, colIdx);
        for(Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            out.map.put(key, value);
        }
        return out;
    }
    
}
