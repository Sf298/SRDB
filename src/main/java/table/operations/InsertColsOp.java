
package table.operations;

import databasepackage.MyTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * The InsertColsOp class is an operation for the MyTable.runOp() function. It 
 * creates a new table with any added columns into specified positions of this
 * table. The columns are then filled with the specified default value(s). The
 * getResult() method of the InsertColsOp class returns value returns a MyTable
 * object.
 * @author Saud Fatayerji
 */
public final class InsertColsOp implements SequentialInterface {
    
    private MyTable outTable;
    
    private final String[] newColTitles;
    private final String[] oldTitles;
    private final int[] poss;
    private final String[] defaultValues;
    private final MyTable columns;
    
    /**
     * Creates a new InsertColsOp object. Supports two types of input, the first
     * requires oldTableTitles, newTableName, positions and columns. The second
     * requires oldTableTitles, newTableName, positions, newColTitles,
     * defaultValues.
     * @param oldTableTitles The titles of the columns in the original table.
     * @param newTableName The name for the new table.
     * @param positions The indexes of the columns to insert into. Similar to
     * ArrayList.add(int index, E element).
     * @param columns A MyTable of the columns to insert.
     * @param newColTitles The titles of the columns that will be created.
     * @param defaultValues The values that the columns will be filled with.
     */
    private InsertColsOp(String[] oldTableTitles, String newTableName, int[] positions, MyTable columns, String[] newColTitles, String[] defaultValues) {
        this.oldTitles = oldTableTitles;
        this.poss = positions;
        
        this.columns = columns;
        
        this.newColTitles = newColTitles;
        this.defaultValues = defaultValues;
        
        this.outTable = new MyTable(newTableName, getNewTitles());
    }
    
    /**
     * Creates a new InsertColsOp object. Supports two types of input, the first
     * requires oldTableTitles, newTableName, positions and columns. The second
     * requires oldTableTitles, newTableName, positions, newColTitles,
     * defaultValues.
     * @param oldTableTitles The titles of the columns in the original table.
     * @param newTableName The name for the new table.
     * @param positions The indexes of the columns to insert into. Similar to
     * ArrayList.add(int index, E element).
     * @param newColTitles The titles of the columns that will be created.
     * @param defaultValues The values that the columns will be filled with.
     */
    public InsertColsOp(String[] oldTableTitles, String newTableName, int[] positions, String[] newColTitles, String[] defaultValues) {
        this(oldTableTitles, newTableName, positions, null, newColTitles, defaultValues);
    }
    
    /**
     * Creates a new InsertColsOp object. Supports two types of input, the first
     * requires oldTableTitles, newTableName, positions and columns. The second
     * requires oldTableTitles, newTableName, positions, newColTitles,
     * defaultValues.
     * @param oldTableTitles The titles of the columns in the original table.
     * @param newTableName The name for the new table.
     * @param position The index of the column to insert into. Similar to
     * ArrayList.add(int index, E element).
     * @param newColTitle The title of the column that will be created.
     * @param defaultValue The values that the column will be filled with.
     */
    public InsertColsOp(String[] oldTableTitles, String newTableName, int position, String newColTitle, String defaultValue) {
        this(oldTableTitles, newTableName, new int[] {position}, new String[] {newColTitle}, new String[] {defaultValue});
    }
    
    /**
     * Creates a new InsertColsOp object. Supports two types of input, the first
     * requires oldTableTitles, newTableName, positions and columns. The second
     * requires oldTableTitles, newTableName, positions, newColTitles,
     * defaultValues.
     * @param oldTableTitles The titles of the columns in the original table.
     * @param newTableName The name for the new table.
     * @param positions The indexes of the columns to insert into. Similar to
     * ArrayList.add(int index, E element).
     * @param columns A MyTable of the columns to insert.
     */
    public InsertColsOp(String[] oldTableTitles, String newTableName, int[] positions, MyTable columns) {
        this(oldTableTitles, newTableName, positions, columns, null, null);
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
            InsertColsOp o = ((InsertColsOp) op[i]);
            outTable.getRows().putAll(o.outTable.getRows());
        }
    }

    @Override
    public InsertColsOp copy() {
        InsertColsOp out = new InsertColsOp(oldTitles, outTable.getTableName(), poss, columns, newColTitles, defaultValues);
        out.outTable = new MyTable(outTable);
        return out;
    }
    
    private static String[] insertCols(String[] arr, int[] poss, String[] values) {
        if(arr==null || poss==null || values==null) return null;
        
        ArrayList<String> out = new ArrayList<>(Arrays.asList(arr));
        for(int i=0; i<values.length; i++) {
            if(poss[i] == -1)
                out.add(values[i]);
            else
                out.add(poss[i], values[i]);
        }
        return out.toArray(new String[out.size()]);
        /*
        String[] newRow = new String[arr.length + values.length];
        HashSet<Integer> idxs = intArr2Set(poss);
        int mod = 0;
        for(int i=0; i<newRow.length; i++) {
            while(idxs.contains(i+mod)) {
                mod++;
            }
            newRow[i] = arr[i+mod];
        }
        return newRow;*/
    }
    
    private static HashSet<Integer> intArr2Set(int[] arr) {
        HashSet<Integer> set = new HashSet<>();
        for(int i : arr)
            set.add(i);
        return set;
    }

    @Override
    public String[] processRow(String key, String[] row) {
        if(defaultValues != null)
            return insertCols(row, poss, defaultValues);
        else
            return insertCols(row, poss, columns.getRow(key));
    }

    @Override
    public String[] getNewTitles() {
        String[] cols2Add = (columns==null) ? newColTitles : columns.getColTitles();
        return insertCols(oldTitles, poss, cols2Add);
    }
    
}
