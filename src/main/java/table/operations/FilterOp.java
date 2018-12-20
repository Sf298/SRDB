
package table.operations;

import databasepackage.Filter;
import databasepackage.MyTable;

/**
 * The FilterOp class is an operation for the MyTable.runOp() function. It
 * filters the rows in the table depending on the provided filter. The
 * getResult() method of the FilterOp class returns value returns a MyTable
 * object.
 * @author Saud Fatayerji
 */
public final class FilterOp implements SequentialInterface {
    
    private final Filter<String, String[]> filter;
    
    private final String newTableName;
    private final String[] titles;
    private MyTable outTable;
    
    /**
     * Creates an FilterOp object.
     * @param newTableName The name for the new table.
     * @param titles The column titles of the output table.
     * @param f The filter to use.
     */
    public FilterOp(String newTableName, String[] titles, Filter<String, String[]> f) {
        this.filter = f;
        this.newTableName = newTableName;
        this.titles = titles;
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
        if(newRow != null)
            outTable.setRow(key, row);
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            FilterOp o = ((FilterOp) op[i]);
            outTable.getRows().putAll(o.outTable.getRows());
        }
    }

    @Override
    public FilterOp copy() {
        FilterOp out = new FilterOp(newTableName, titles, filter);
        out.outTable = new MyTable(outTable);
        return out;
    }

    @Override
    public String[] processRow(String key, String[] row) {
        if(filter.shouldKeep(key, row))
            return row;
        else
            return null;
    }

    @Override
    public String[] getNewTitles() {
        return titles;
    }
    
}
