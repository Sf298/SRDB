# Saud's Relational Database
This library is a relational database toolkit intended for light weight use cases such as prototype or personal projects, where a full DBMS may be unnecessary. This implementation is fully built using java making it highly portable.

## Useage Instructions
### Creating Tables
Table models are stored in the MyTable class. This class also provides a number of funtions to modify the data.
The code below creates a table called 'People' with four columns titled "First Name", "Last Name", "Phone Num" and "Email".
~~~~
MyTable personT = new MyTable("People", new String[] {"First Name", "Last Name", "Phone Num", "Email"});
~~~~
or
~~~~
MyTable personT = new MyTable("People", "First Name", "Last Name", "Phone Num", "Email");
~~~~

### Modifying Table Content
Adding new records to a table is simple, however the number of added records must be equal to the number of columns (excluding the ID column which is managed by the database).
~~~~
personT.addRow("John", "Smith", "012345678", "john.smith@gmail.com");
~~~~

To edit a row, you can use the setRow() method. This works similarly to the addRow method but also takes a recordId parameter.
~~~~
personT.addRow("John", "Smith", "876543210", "john.smith@gmail.com");
~~~~
Additionally you can set the content of a specific cell with the setCellValue() method.

Rows are deleted with the removeRow() function.

### Parallel Operations
The MyTable class supports a system to perform batch operations on the content of a table. While users can create custom operations by implementing the 'TableOperationInterface' or 'SequentialInterface' iterfaces, there are a number of predefined operations (listed below). These can be accessed as shown below.
~~~~
UniqueOp uniqueOp = table.getUniqueOp(selectedColTitle);
SumOp sumOp = table.getSumOp(selectedColTitle);
~~~~

These operations only contain the instructions for the operation. They must be executed using the runOp() method before the results become available.
~~~~
Double result = (Double) table.runOp(true, 4, sumOp);
~~~~
Multiple operations may be run in parallel on the same table using the runOps() method.
~~~~
Object[] results = table.runOp(true, -1, uniqueOp, sumOp);
~~~~

### Sequential Operations
Often a table will need to be passed through one operation before it can be passed into the next. For these purposes, the runOpsSequentially() method was created. Using this function rather than a series of calls to runOp() can dramatically improve performance of the program. The code below assumes that there is are 'Pets', 'Appointment', and 'People' tables. It will find any records where a client owes money.
~~~~
JoinTableOp op1 = appointmentsT.getJoinTableOp("", "Pet", petT);
JoinTableOp op2 = op1.getResult().getJoinTableOp("", "Pet.Owner", personT);
TrimColTitlesOp op3 = op2.getResult().getTrimColTitlesOp("");
SelectColsOp op4 = op3.getResult().getSelectColsOp("", "Amount", "Payment", "Balance", "First Name", "Last Name");
CatColumnsOp op5 = op4.getResult().getCatColumnsOp("", "Name", " ", "First Name", "Last Name");
MyTable table = appointmentsT.runOpsSequentially("Money Owed", true, -1, op1, op2, op3, op4, op5);
~~~~

### Available Operations
Summary operations
* Avg (mean)
* UniqueOp
* SumOp
* AvgMeanOp
* GroupOps

Table operations
* CatColumnsOp
* FilterOp
* InsertColsOp
* SelectColsOp
* RemoveColsOp
* JoinTableOp
* TrimColTitlesOp

### Saving and Loading
Tables can be saved and loaded in XML format using the appropriate static functions of the MyTable class. Multiple tables can be saved to one file, however each call to the saveTables method overwrites the contents of the file.
~~~~
// save
MyTable.saveTables(file, personT, horseT, appointmentsT);

// load
HashMap<String, MyTable> tables = MyTable.loadTables(HorseDBMain.dbSaveFile);
if(tables != null) {
    personT = tables.get("People");
    petT = tables.get("Pets");
    appointmentsT = tables.get("Appointments");
}
~~~~


### Table Viewer
The MyTableViewer class provides a simple tool for allowing users to view and edit tables. It can be loaded in two ways, editable or not editable. When not editable, the buttons used to edit the table are hidden, however other features like search and apply function are still available. If a parentFrame is provided, it will be hidden while the table viewer is visible.
~~~~
MyTableViewer viewer = new MyTableViewer(table, isEditable, parentFrame);
viewer.openViewer();
~~~~
