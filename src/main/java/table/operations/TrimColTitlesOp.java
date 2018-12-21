/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table.operations;

import databasepackage.MyTable;

/**
 *
 * @author saud
 */
public class TrimColTitlesOp implements SequentialInterface {
    
    private MyTable outTable;
    private String[] oldColTitles;
    
    public TrimColTitlesOp(String newTableName, String[] oldColTitles) {
        this.oldColTitles = oldColTitles;
        outTable = new MyTable(newTableName, getNewTitles());
    }

    @Override
    public String[] processRow(String key, String[] row) {
        return row;
    }

    @Override
    public final String[] getNewTitles() {
        String[] newTitles = new String[oldColTitles.length];
        for(int i=0; i<oldColTitles.length; i++) {
            newTitles[i] = oldColTitles[i].substring(oldColTitles[i].lastIndexOf(".")+1);
        }
        return newTitles;
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
        outTable.getRows().put(key, processRow(key, row));
    }

    @Override
    public void mergeResultsIntoThis(TableOperationInterface... op) {
        for(TableOperationInterface o : op) {
            TrimColTitlesOp jto = (TrimColTitlesOp) o;
            outTable.getRows().putAll(jto.getResult().getRows());
        }
    }

    @Override
    public Object copy() {
        TrimColTitlesOp out = new TrimColTitlesOp(outTable.getTableName(), oldColTitles);
        out.outTable = new MyTable(outTable);
        return out;
    }
    
}
