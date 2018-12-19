/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasepackage;

import imported.Timer;

/**
 *
 * @author saud
 */
public class DatabasePackageTester {
    
    public static void main(String[] args) throws CloneNotSupportedException {
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
    
    
}
