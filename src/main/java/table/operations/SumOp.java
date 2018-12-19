
package table.operations;

/**
 * The SumOp class is an operation for the MyTable.runOp() function. It
 * filters the rows in the table depending on the provided filter. The
 * getResult() method of the SumOp class returns value returns a Double
 * object.
 * @author Saud Fatayerji
 */
public class SumOp implements TableOperationInterface {
    
    private double sum;
    
    private final String colTitle;
    private final int colIdx;
    
    /**
     * 
     * @param colTitle The title of the column to process.
     * @param colIdx The index of the column to process.
     */
    public SumOp(String colTitle, int colIdx) {
        this.colTitle = colTitle;
        this.colIdx = colIdx;
    }

    @Override
    public String getColTitle() {
        return colTitle;
    }

    @Override
    public Double getResult() {
        return sum;
    }

    @Override
    public void runOp(String key, String[] row) {
        try {
            double tempVal = Double.parseDouble(row[colIdx]);
            sum += tempVal;
        } catch(NumberFormatException e) {}
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            SumOp o = ((SumOp) op[i]);
            sum += o.sum;
        }
    }

    @Override
    public SumOp copy() {
        SumOp out = new SumOp(colTitle, colIdx);
        out.sum = sum;
        return out;
    }
    
}
