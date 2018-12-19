
package table.operations;

import databasepackage.MyTable;
import java.util.HashMap;
import java.util.Map;

/**
 * The GroupOps class is an operation for the MyTable.runOp() function. It 
 * creates a new table where repeated elements in the specified column are
 * grouped together. The provided ops are then run separately for each of
 * the grouped rows. Similar to the SQL 'GROUP BY' statement. The getResult()
 * method of the GroupOps class returns value returns a MyTable object.
 * @author Saud Fatayerji
 */
public class GroupOps implements TableOperationInterface {
    
    private final String newTableName;
    private final String colToGroupBy;
    private final int colIdxToGroupBy;
    private final TableOperationInterface[] ops;
    
    private HashMap<String, TableOperationInterface[]> map = new HashMap<>();
    
    /**
     * Creates a new GroupOps object.
     * @param newTableName The name for the new table.
     * @param colToGroupBy The title of the column to group.
     * @param colIdxToGroupBy The indexes of the columns on which the ops operate.
     * @param ops The operations to run on the grouped records.
     */
    public GroupOps(String newTableName, String colToGroupBy, int colIdxToGroupBy, TableOperationInterface... ops) {
        this.newTableName = newTableName;
        this.colToGroupBy = colToGroupBy;
        this.colIdxToGroupBy = colIdxToGroupBy;
        this.ops = ops;
    }
    
    @Override
    public String getColTitle() {
        return null;
    }

    @Override
    public MyTable getResult() {
        String[] newTitles = getNewTitles();
        MyTable table = new MyTable(newTableName, newTitles);
        for(Map.Entry<String, TableOperationInterface[]> entry : map.entrySet()) {
            String key = entry.getKey();
            TableOperationInterface[] value = entry.getValue();
            table.addRow(MyTable.insert(TableOperationInterface.getOpsResultsAsStr(value), key, 0));
        }
        return table;
    }

    @Override
    public void runOp(String key, String[] row) {
        if(!map.containsKey(row[colIdxToGroupBy]))
            map.put(row[colIdxToGroupBy], TableOperationInterface.copyOpsArr(ops));
        for(TableOperationInterface op : map.get(row[colIdxToGroupBy]))
            op.runOp(key, row);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... opInter) {
        for(int i=0; i<opInter.length; i++) {
            GroupOps go = ((GroupOps) opInter[i]);
            map.putAll(go.map);
        }
    }

    @Override
    public Object copy() {
        GroupOps out = new GroupOps(newTableName, colToGroupBy, colIdxToGroupBy, ops);
        out.map = new HashMap<>();
        for (Map.Entry<String, TableOperationInterface[]> entry : map.entrySet()) {
            String key = entry.getKey();
            TableOperationInterface[] value = entry.getValue();
            out.map.put(key, TableOperationInterface.copyOpsArr(value));
        }
        return out;
    }
    
    private String[] getNewTitles() {
        String[] newTitles = new String[ops.length+1];
        newTitles[0] = colToGroupBy;
        for(int i=1; i<newTitles.length; i++) {
            newTitles[i] = ops[i-1].getColTitle();
        }
        return newTitles;
    }
    
}
