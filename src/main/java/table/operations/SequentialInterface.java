/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.operations;

/**
 *
 * @author saud
 */
public interface SequentialInterface extends TableOperationInterface {
    
    public String[] processRow(String key, String[] row);
    
    public String[] getNewTitles();
    
    /**
     * Clones an array 
     * @param ops
     * @return 
     */
    public static SequentialInterface[] copyOpsArr(SequentialInterface... ops) {
        SequentialInterface[] out = new SequentialInterface[ops.length];
        for(int i=0; i<out.length; i++) {
            out[i] = (SequentialInterface) ops[i].copy();
        }
        return out;
    }
    
}
