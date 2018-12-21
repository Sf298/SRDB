
package table.operations;

import databasepackage.MyTable;
import java.util.ArrayList;

/**
 * The CatColumnsOp class is an operation for the MyTable.runOp() function. It
 * concatenates the values of columns together. The getResult() method of the
 * CatColumnsOp class returns value returns a MyTable object.
 * @author Saud Fatayerji
 */
public final class CatColumnsOp implements SequentialInterface {
    
    private final String[] oldTableTitles;
    private final ArrayList<Integer> colsToJoin;
    private final String separator;
    private final String newColName;
    
    private MyTable table;
    
    /**
     * Creates an CatColumnsOp object.
     * @param newTableName The name for the new table.
     * @param newColName The title of the column that will be created.
     * @param separator The separator used when concatenating the values in the
     * columns.
     * @param oldTableTitles The titles of the columns in the original table.
     * @param colsToJoin The titles of the columns to join. The order provided
     * here will match the order that the values are joined in. These will be
     * removed from the resulting table.
     */
    public CatColumnsOp(String newTableName, String newColName, String separator, String[] oldTableTitles, int[] colsToJoin) {
        this.oldTableTitles = oldTableTitles;
        this.separator = separator;
        this.newColName = newColName;
        
        this.colsToJoin = new ArrayList();
        for (int i : colsToJoin) {
            this.colsToJoin.add(i);
        }
        
        String[] titles = getNewTitles();
        table = new MyTable(newTableName, titles);
    }

    @Override
    public String getColTitle() {
        return null;
    }

    @Override
    public MyTable getResult() {
        return table;
    }

    @Override
    public void runOp(String key, String[] row) {
        String[] newRow = processRow(key, row);
        table.setRow(key, newRow);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(TableOperationInterface o : op) {
            CatColumnsOp cco = (CatColumnsOp) o;
            table.getRows().putAll(cco.getResult().getRows());
        }
    }

    @Override
    public CatColumnsOp copy() {
        int[] tempArr = new int[colsToJoin.size()];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = colsToJoin.get(i);
        }
        CatColumnsOp out = new CatColumnsOp(table.getTableName(), newColName, separator, oldTableTitles, tempArr);
        out.table = new MyTable(table);
        return out;
    }

    @Override
    public String[] processRow(String key, String[] row) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<colsToJoin.size(); i++) {
            if(i>0)
                sb.append(separator);
            sb.append(row[colsToJoin.get(i)]);
        }
        
        String[] newRow = new String[table.getColCount()];
        int mod = 0;
        for(int i=0; i<newRow.length-1; i++) {
            while(this.colsToJoin.contains(i+mod)) {
                mod++;
            }
            newRow[i] = row[i+mod];
        }
        newRow[newRow.length-1] = sb.toString();
        return newRow;
    }

    @Override
    public String[] getNewTitles() {
        String[] titles = new String[oldTableTitles.length - colsToJoin.size() + 1];
        int mod = 0;
        for(int i=0; i<titles.length-1; i++) {
            while(this.colsToJoin.contains(i+mod)) {
                mod++;
            }
            titles[i] = oldTableTitles[i+mod];
        }
        titles[titles.length-1] = newColName;
        return titles;
    }
    
}
