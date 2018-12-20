/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasepackage;

import imported.SampleDataGen;
import imported.Timer;
import table.operations.CatColumnsOp;
import table.operations.InsertColsOp;
import table.operations.RemoveColsOp;

/**
 *
 * @author saud
 */
public class DatabasePackageTester {
    
    public static void main(String[] args) throws CloneNotSupportedException {
        seqOpsSpeedTest();
    }
    public static void oldTests() {
        MyTable people = new MyTable("People", new String[] {"First Name", "Last Name", "Phone Number"});
        people.addRow("saud", "f", "0552039784");
        people.addRow("sult", "b", "00000000");

        MyTable horses = new MyTable("Horses", new String[] {"Owner", "Name"});
        horses.addRow("0", "Eva");
        horses.addRow("0", "dusty");
        horses.addRow("1", "marshal");

        MyTable appointments = new MyTable("Appointments", new String[] {"Date", "Horse", "Diagnosis", "Filtration", "Treatment", "Amount", "Payment", "Place"});
        appointments.addRow("24/24/24", "1", "cut", "ear", "Anti-B", "15", "0 SAR", "Jeddah");
        appointments.addRow("22/22/22", "0", "bruise", "foot", "Ice", "32", "12 SAR", "Riyadh");
        appointments.addRow("11/11/11", "2", "broken", "leg", "painK", "200", "100 SAR", "Dubai");
        
        //generalTests(people, horses, appointments);
        //moneyOwed(people, horses, appointments);
        
        /*MyTableViewer v = new MyTableViewer(horses, true, null);
        v.setRefKeys(new MyTable[] {people, null}, new String[][] {new String[] {"First Name", "Last Name"}, null});
        v.setSize(500, 500);
        v.setVisible(true);*/
        
        MyTable t = null;
        /*Timer timer1 = new Timer("timer 1");
        for (int i = 0; i < 1000000; i++) {
            t = (MyTable) appointments.reductionOp(appointments.getSelectOp("New Name", "Date", "Diagnosis", "Filtration"));
        }
        timer1.lap();*/
        
        Timer timer2 = new Timer("timer 2");
        for (int i = 0; i < 1000000; i++) {
            t = (MyTable) new MyTable(appointments);
            t.selectColumns("temp", 1, "Date", "Diagnosis", "Filtration");
        }
        timer2.lap();
        
        MyTableViewer v = new MyTableViewer(t, true, null);
        v.setRefKeys(new MyTable[] {people, null}, new String[][] {new String[] {"First Name", "Last Name"}, null});
        v.setSize(500, 500);
        v.setVisible(true);
        
        
        /*Connection con = null;
        Statement stmt = null;
        int result = 0;
        try {
            
            Class.forName("org.hsqldb.jdbcDriver" );
            con = DriverManager.getConnection("jdbc:hsqldb:testdb", "sa", "");
            stmt = con.createStatement();
         
            result = stmt.executeUpdate("CREATE TABLE tutorials_tbl ("
                    + "id INT NOT NULL, title VARCHAR(50) NOT NULL,"
                    + "author VARCHAR(20) NOT NULL, submission_date DATE,"
                    + "PRIMARY KEY (id));");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabasePackageTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(result);*/
    }
    
    public static void generalTests(MyTable people, MyTable horses, MyTable appointments) {
        System.out.println(people.catColumns("Name", " ", "First Name", "Last Name").toString());
        System.out.println(horses.toString());
        System.out.println(horses.join("temp", "Owner", people));
        
        MyTable table = appointments.join("temp1", "Horse", horses);
        table = table.join("temp2", "Horses.Owner", people);
        table.trimColumnNames();
        System.out.println(table);
        
        System.out.println(horses.filterRows("Name", "dusty"));
    }
    
    public static void moneyOwed(MyTable people, MyTable horses, MyTable appointments) {
        MyTable t = appointments;
        t = t.join("horses-appointments", "Horse", horses);
        t = t.join("horses-appointments-owner", "Horses.Owner", people);
        t.trimColumnNames();
        t = t.selectColumns("temp", 1, "Amount", "Payment", "First Name", "Last Name");
        t = t.catColumns("Owner", " ", "First Name", "Last Name");
        //System.out.println(t.reductionOp(t.getGroupOp("Money Owed", "Owner", t.getSumOp("Amount"))));
        //System.out.println(t.avgColumn("Amount"));
    }
    
    public static void seqOpsSpeedTest() {
        SampleDataGen.setSeed(648764);
        MyTable table = new MyTable("People", new String[] {"First Name", "Last Name", "Phone Num", "Email"});
        int personCount = 200000;
        int horseCount =  100000;
        for(int i=0; i<personCount; i++) {
            table.addRow(
                    SampleDataGen.randWord(), SampleDataGen.randWord(),
                    SampleDataGen.randInt(100000000, 999999999)+"", SampleDataGen.randEmail());
        }
        MyTable table2 = new MyTable("Horses", new String[] {"Owner", "Name"});
        for(int i=0; i<horseCount; i++) {
            table2.addRow(SampleDataGen.randInt(0, personCount-1)+"", SampleDataGen.randWord());
        }
        
        
        String newTableName = "TEMP1";
        String[] selectedCols = {"First Name", "Last Name"};
        
        String[] newTitles = new String[] {"new_col1", "new_col2"};
        int[] newPositions = new int[] {-1, -1};
        String[] newDefVals = new String[] {"COL1!", "COL2!"};
        
        Timer t1 = new Timer("Seq timer");
        for (int i = 0; i < 50; i++) {
            CatColumnsOp op1 = table.getCatColumnsOp(newTableName, "Name", " ", selectedCols);
            InsertColsOp op2 = op1.getResult().getInsertColsOp(newTableName, newPositions, newTitles, newDefVals);
            RemoveColsOp op3 = op2.getResult().getRemoveColsOp(newTableName, "Email");
            MyTable actual = table.runOpsSequentially(newTableName, true, 4, op1, op2, op3);
        }
        t1.print();
        
        Timer t2 = new Timer("Old timer");
        for (int i = 0; i < 50; i++) {
            MyTable expected = (MyTable) table.runOp(true, 4, table.getCatColumnsOp(newTableName, "Name", " ", selectedCols));
            expected = (MyTable) expected.runOp(true, 4, expected.getInsertColsOp(newTableName, newPositions, newTitles, newDefVals));
            expected = (MyTable) expected.runOp(true, 4, expected.getRemoveColsOp(newTableName, "Email"));
        }
        t2.print();
    }
    
}
