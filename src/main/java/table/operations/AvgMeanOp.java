
package table.operations;

/**
 * The AvgMeanOp class is an operation for the MyTable.runOp() function. It
 * calculates the mean value of a column. The getResult() method of the
 * AvgMeanOp class returns value returns a Double object.
 * @author Saud Fatayerji
 */
public class AvgMeanOp implements TableOperationInterface {
    
    private double sum;
    private int count;
    
    private final String colTitle;
    private final int colIdx;
    
    /**
     * Creates an AvgMeanOp object.
     * @param colTitle The title of the column to process.
     * @param colIdx The index of the column to process.
     */
    public AvgMeanOp(String colTitle, int colIdx) {
        this.colTitle = colTitle;
        this.colIdx = colIdx;
    }

    @Override
    public String getColTitle() {
        return colTitle;
    }

    @Override
    public Double getResult() {
        if(count==0) return new Double(0);
        return sum/count;
    }

    @Override
    public void runOp(String key, String[] row) {
        try {
            double tempVal = Double.parseDouble(row[colIdx]);
            sum += tempVal;
            count++;
        } catch(NumberFormatException e) {}
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(int i=0; i<op.length; i++) {
            AvgMeanOp o = ((AvgMeanOp) op[i]);
            sum += o.sum;
            count += o.count;
        }
    }

    @Override
    public AvgMeanOp copy() {
        AvgMeanOp out = new AvgMeanOp(colTitle, colIdx);
        out.sum = sum;
        out.count = count;
        return out;
    }
    
}
