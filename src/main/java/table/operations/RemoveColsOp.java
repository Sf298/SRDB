
package table.operations;

import databasepackage.MyTable;
import java.util.HashSet;

/**
 * The RemoveColsOp class is an operation for the MyTable.runOp() function. It 
 * creates a new table with all specified columns removed. The getResult()
 * method of the RemoveColsOp class returns value returns a MyTable object.
 * @author Saud Fatayerji
 */
public class RemoveColsOp implements SequentialInterface {
    
    private MyTable outTable;
    
    private final String[] oldTableTitles;
    private final String[] titlesToRemove;
    private final HashSet<Integer> idxsToRemove;
    
    /**
     * Creates an RemoveColsOp object.
     * @param newTableName The name for the new table.
     * @param titlesToRemove The titles to remove.
     * @param oldTableTitles The titles of the columns in the original table.
     */
    public RemoveColsOp(String[] oldTableTitles, String newTableName, String[] titlesToRemove) {
        this.oldTableTitles = oldTableTitles;
        this.titlesToRemove = titlesToRemove;
        this.idxsToRemove = intArr2Set(getColIdxsByNames(oldTableTitles, titlesToRemove));
        this.outTable = new MyTable(newTableName, getNewTitles());
    }
    private static HashSet<Integer> intArr2Set(int[] arr) {
        HashSet<Integer> set = new HashSet<>();
        for(int i : arr)
            set.add(i);
        return set;
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
        outTable.setRow(key, newRow);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            RemoveColsOp o = ((RemoveColsOp) op[i]);
            outTable.getRows().putAll(o.outTable.getRows());
        }
    }

    @Override
    public RemoveColsOp copy() {
        RemoveColsOp out = new RemoveColsOp(oldTableTitles, outTable.getTableName(), titlesToRemove);
        out.outTable = new MyTable(outTable);
        return out;
    }
    
    private static String[] remIdxs(HashSet<Integer> idxs, String[] arr) {
        String[] newRow = new String[arr.length - idxs.size()];
        int mod = 0;
        for(int i=0; i<newRow.length; i++) {
            while(idxs.contains(i+mod)) {
                mod++;
            }
            newRow[i] = arr[i+mod];
        }
        return newRow;
    }
    
    /**
     * Gets the index of the specified column.
     * @param colTitle the name of the specified column.
     * @return The index of the specified column.
     */
    private int getColIdxByName(String[] titles, String colTitle) {
        for(int i=0; i<titles.length; i++) {
            if(colTitle.equals(titles[i]))
                return i;
        }
        throw new RuntimeException("Cannot find title with name: "+colTitle);
    }
    
    /**
     * Gets the indexes of the specified columns.
     * @param colTitles the name of the specified columns.
     * @return The indexes of the specified column.
     */
    private int[] getColIdxsByNames(String[] originalTitles, String... colTitles) {
        int[] colIndexes = new int[colTitles.length];
        for(int i=0; i<colTitles.length; i++) {
            colIndexes[i] = getColIdxByName(originalTitles, colTitles[i]);
        }
        return colIndexes;
    }

    @Override
    public String[] processRow(String key, String[] row) {
        return remIdxs(idxsToRemove, row);
    }

    @Override
    public String[] getNewTitles() {
        return remIdxs(idxsToRemove, oldTableTitles);
    }
    
}
