
package table.operations;

import databasepackage.MyTable;
import java.util.ArrayList;

/**
 * The JoinTableOp class is an operation for the MyTable.runOp() function. It
 * joins together tables on a reference column. The getResult() method of the
 * JoinTableOp class returns value returns a MyTable object.
 * @author Saud Fatayerji
 */
public class JoinTableOp implements SequentialInterface {
    
    private final String refColTitle;
    private final MyTable pkTable;
    private final MyTable rkTable;
    private final int colToRemove;
    private MyTable outTable;
    
    /**
     * Creates a JoinTableOp object.
     * @param newTableName The name for the new table.
     * @param refColTitle The title of the reference column in the reference table.
     * @param pkTable The table containing the primary key.
     * @param rkTable The table containing the reference column used in the join.
     */
    public JoinTableOp(String newTableName, String refColTitle, MyTable pkTable, MyTable rkTable) {
        this.pkTable = pkTable;
        this.rkTable = rkTable;
        this.refColTitle = refColTitle;
        this.colToRemove = rkTable.getColIdxByName(refColTitle);
        
        
        this.outTable = new MyTable(newTableName, getNewTitles());
    }

    @Override
    public String getColTitle() {
        return null;
    }

    @Override
    public MyTable getResult() {
        return outTable;
    }

    @Override
    public void runOp(String key, String[] row) {
        String[] newRow = processRow(key, row);
        if(newRow!=null)
            outTable.setRow(key, newRow);
        /*String[] newRow = new String[outTable.getColCount()];
        String[] refRow = null;
        for(int j=0; j<newRow.length; j++) {
            if(j<rkTable.getColCount()) {
                newRow[j] = row[j];
                if(colToRemove == j)
                    refRow = pkTable.getRow(newRow[j]);
            } else if(refRow != null) {
                newRow[j] = refRow[j-rkTable.getColCount()];
            }
        }
        outTable.setRow(key, newRow);*/
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(TableOperationInterface o : op) {
            JoinTableOp jto = (JoinTableOp) o;
            outTable.getRows().putAll(jto.getResult().getRows());
        }
    }

    @Override
    public Object copy() {
        JoinTableOp out = new JoinTableOp(outTable.getTableName(), refColTitle, pkTable, rkTable);
        out.outTable = new MyTable(outTable);
        return out;
    }

    @Override
    public String[] processRow(String key, String[] row) {
        String[] newRow = new String[outTable.getColCount()];
        int i=0;
        for(; i<colToRemove; i++) {
            newRow[i] = row[i];
        }
        i++;
        for(; i<rkTable.getColCount(); i++) {
            newRow[i-1] = row[i];
        }
        String[] pkRow = pkTable.getRow(row[colToRemove]);
        if(pkRow == null)
            return null;
        i--;
        for(; i<newRow.length; i++) {
            int temp = i-(rkTable.getColCount()-1);
            newRow[i] = pkRow[temp];
        }
        return newRow;
    }

    @Override
    public String[] getNewTitles() {
        ArrayList<String> newTitles = new ArrayList<>();
        for(int i=0; i<rkTable.getColTitles().length; i++) {
            newTitles.add(rkTable.getTableName()+"."+rkTable.getColTitle(i));
        }
        for(int i=0; i<pkTable.getColTitles().length; i++) {
            newTitles.add(pkTable.getTableName()+"."+pkTable.getColTitle(i));
        }
        newTitles.remove(colToRemove);
        return newTitles.toArray(new String[newTitles.size()]);
    }
    
}
