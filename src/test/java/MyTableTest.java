/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import databasepackage.MyTable;
import imported.SampleDataGen;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import table.operations.CatColumnsOp;
import table.operations.InsertColsOp;
import table.operations.RemoveColsOp;

/**
 *
 * @author saud
 */
public class MyTableTest {
    
    MyTable table;
    MyTable table2;
    
    public MyTableTest() {
        SampleDataGen.setSeed(648764);
        table = new MyTable("People", new String[] {"First Name", "Last Name", "Phone Num", "Email"});
        int personCount = 20000;
        int horseCount =  10000;
        for(int i=0; i<personCount; i++) {
            table.addRow(
                    SampleDataGen.randWord(), SampleDataGen.randWord(),
                    SampleDataGen.randInt(100000000, 999999999)+"", SampleDataGen.randEmail());
        }
        

        table2 = new MyTable("Horses", new String[] {"Owner", "Name"});
        for(int i=0; i<horseCount; i++) {
            table2.addRow(SampleDataGen.randInt(0, personCount-1)+"", SampleDataGen.randWord());
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    
    @Test
    public void testSequential() {
        String newTableName = "TEMP1";
        String[] selectedCols = {"First Name", "Last Name"};
        
        String[] newTitles = new String[] {"new_col1", "new_col2"};
        int[] newPositions = new int[] {-1, -1};
        String[] newDefVals = new String[] {"COL1!", "COL2!"};
        
        CatColumnsOp op1 = table.getCatColumnsOp(newTableName, "Name", " ", selectedCols);
        InsertColsOp op2 = op1.getResult().getInsertColsOp(newTableName, newPositions, newTitles, newDefVals);
        RemoveColsOp op3 = op2.getResult().getRemoveColsOp(newTableName, "Email");
        MyTable actual = table.runOpsSequentially(newTableName, true, 4, op1, op2, op3);
        
        
        MyTable expected = (MyTable) table.runOp(true, 4, table.getCatColumnsOp(newTableName, "Name", " ", selectedCols));
        expected = (MyTable) expected.runOp(true, 4, expected.getInsertColsOp(newTableName, newPositions, newTitles, newDefVals));
        expected = (MyTable) expected.runOp(true, 4, expected.getRemoveColsOp(newTableName, "Email"));
        
        boolean out = actual.equals(expected);
        assertTrue(out);
    }
    
    
    @Test
    public void testFilterOp() {
        String selectedCol = "First Name";
        String selectedVal = table.getCellValue(selectedCol, "1");
        MyTable actual = (MyTable) table.runOp(true, 4, table.getFilterOp(selectedCol, selectedVal));
        
        MyTable expected = table.filterRows(selectedCol, selectedVal);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testCatColumnsOp() {
        String[] selectedCols = {"First Name", "Last Name"};
        MyTable actual = (MyTable) table.runOp(true, 4, table.getCatColumnsOp(table.getTableName(), "Name", " ", selectedCols));
        
        MyTable expected = new MyTable(table);
        expected = expected.catColumns("Name", " ", selectedCols);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testJoinTableOp() {
        String newTableName = "TEMP1";
        String refColTitle = "Owner";
        MyTable actual = (MyTable) table2.runOp(true, 4, table2.getJoinTableOp(newTableName, refColTitle, table));
        
        MyTable expected = new MyTable(table2);
        expected = expected.join(newTableName, refColTitle, table);
        
        boolean out = actual.equals(expected);
        assertTrue(out);
    }
    
    /*@Test
    public void testSelectColOp() {
        String selectedCol = "First Name";
        HashMap<String, String> actual = (HashMap<String, String>) table.runOp(true, 4, table.getSelectColOp(selectedCol));
        
        HashMap<String, String> expected = table.getColumn(selectedCol, true);
        
        assertTrue(expected.equals(actual));
    }*/
    
    @Test
    public void testInsertColsOp() {
        String newTableName = "TEMP1";
        String[] newTitles = new String[] {"new_col1", "new_col2"};
        int[] newPositions = new int[] {-1, -1};
        String[] newDefVals = new String[] {"COL1!", "COL2!"};
        
        MyTable actual = (MyTable) table.runOp(true, 4, table.getInsertColsOp(newTableName, newPositions, newTitles, newDefVals));
        
        MyTable expected = new MyTable(table);
        for(int i=0; i<newTitles.length; i++) {
            expected.insertColumn(newTitles[i], newPositions[i], newDefVals[i]);
        }
        expected.setTableName(newTableName);
        
        boolean out = actual.equals(expected);
        assertTrue(out);
        
        
        
        newTitles = new String[] {"First Name", "Last Name"};
        MyTable cols = (MyTable) table.runOp(true, 4, table.getSelectColsOp(newTableName, newTitles));
        actual = (MyTable) table.runOp(true, 4, table.getInsertColsOp(newTableName, newPositions, cols));
        
        expected = new MyTable(table);
        for(int i=0; i<newTitles.length; i++) {
            HashMap<String,String> temp = table.getColumn(newTitles[i], false);
            expected.addColumn(newTitles[i], temp);
        }
        expected.setTableName(newTableName);
        
        out = actual.equals(expected);
        assertTrue(out);
    }
    
    @Test
    public void testSelectColsOp() {
        String newTableName = "TEMP1";
        String[] selectedCols = {"Last Name", "Email"};
        MyTable actual = (MyTable) table.runOp(true, 4, table.getSelectColsOp(newTableName, selectedCols));
        
        MyTable expected = new MyTable(newTableName, selectedCols);
        int[] idxs = table.getColIdxsByNames(selectedCols);
        for (Map.Entry<String, String[]> entry : table.getRows().entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            
            String[] temp = new String[idxs.length];
            for(int i=0; i<idxs.length; i++) {
                temp[i] = value[idxs[i]];
            }
            expected.setRow(key, temp);
        }
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testRemoveColsOp() {
        String newTableName = "TEMP1";
        String[] selectedCols = {"First Name", "Last Name"};
        MyTable actual = (MyTable) table.runOp(true, 4, table.getRemoveColsOp(newTableName, selectedCols));
        
        MyTable expected = new MyTable(table);
        expected.removeColumns(selectedCols);
        expected.setTableName(newTableName);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testUniqueOp() {
        String selectedCol = "Last Name";
        HashSet<String> actual = (HashSet<String>) table.runOp(true, 4, table.getUniqueOp(selectedCol));
        
        HashSet<String> expected = new HashSet<>();
        int idx = table.getColIdxByName(selectedCol);
        for (Map.Entry<String, String[]> entry : table.getRows().entrySet()) {
            String[] value = entry.getValue();
            
            expected.add(value[idx]);
        }
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSumOp() {
        String selectedCol = "Phone Num";
        Double actual = (Double) table.runOp(true, 4, table.getSumOp(selectedCol));
        
        double sum = 0;
        int idx = table.getColIdxByName(selectedCol);
        for (Map.Entry<String, String[]> entry : table.getRows().entrySet()) {
            String[] value = entry.getValue();
            
            double tempVal = Double.parseDouble(value[idx]);
            sum += tempVal;
        }
        Double expected = sum;
        
        System.out.println("testSumOp-1: exp("+expected+"), act("+actual+")");
        assertEquals(expected, actual);
        
        
        selectedCol = "First Name";
        actual = (Double) table.runOp(true, 4, table.getSumOp(selectedCol));
        System.out.println("testSumOp-2: exp("+expected+"), act("+actual+")");
        assertEquals(new Double(0), actual);
    }
    
    @Test
    public void testAvgOp() {
        String selectedCol = "Phone Num";
        Double actual = (Double) table.runOp(true, 4, table.getAvgOp(selectedCol));
        
        double sum = 0;
        int count = 0;
        int idx = table.getColIdxByName(selectedCol);
        for (Map.Entry<String, String[]> entry : table.getRows().entrySet()) {
            String[] value = entry.getValue();
            
            double tempVal = Double.parseDouble(value[idx]);
            sum += tempVal;
            count++;
        }
        Double expected = sum/count;
        
        System.out.println("testAvgOp-1: exp("+expected+"), act("+actual+")");
        assertEquals(expected, actual);
        
        
        selectedCol = "First Name";
        actual = (Double) table.runOp(true, 4, table.getAvgOp(selectedCol));
        System.out.println("testAvgOp-2: exp("+expected+"), act("+actual+")");
        assertEquals(new Double(0), actual);
    }
    
    
    @Test
    public void testEquals() {
        String newTableName = "TEMP1";
        String[] selectedCols = {"Last Name", "Email"};
        
        MyTable expected = new MyTable(newTableName, selectedCols);
        expected.addRow("Fatayerji", "aaa@bbb.com");
        
        MyTable actual = new MyTable(newTableName, selectedCols);
        actual.addRow("Fatayerji", "aaa@bbb.com");
        
        boolean out = actual.equals(expected);
        assertTrue(out);
    }
    
}
