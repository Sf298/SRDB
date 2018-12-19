
package databasepackage;

import imported.GetterFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import table.operations.TableOperationInterface;

/**
 * The MyTableViewer class provides a GUI Frame for viewing and editing the
 * respective MyTable class.
 * @author Saud Fatayerji
 */
public class MyTableViewer extends JFrame {
    
    private JTable table;
    private TableRowSorter<TableModel> sorter;
    private DefaultTableModel model;
    private JPanel mainPanel;
    private JFrame myParent;
    private final JFrame thisFrame;
    private final MyTable t;
    private MyTable[] refKeyTables;
    private String[][] refKeyTitles;
    
    /**
     * Creates a new viewer frame.
     * @param table The table model to view.
     * @param editable If true, the frame is provided with buttons to edit the
     * the table contents.
     * @param parent The parent frame. If provided, the parent will be hidden
     * while this frame is visible.
     */
    public MyTableViewer(MyTable table, boolean editable, JFrame parent) {
        myParent = parent;
        this.t = table;
        populateFrame(editable);
        thisFrame = this;
        refKeyTables = new MyTable[table.getColTitles().length];
        refKeyTitles = new String[table.getColTitles().length][];
        super.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                if(myParent!=null)
                    myParent.setVisible(true);
                /*else
                    System.exit(0);*/
            }
        });
    }
    
    /**
     * Sets up the reference keys and tables. Any columns with non-null elements
     * will be treated as reference columns.
     * @param refKeyTables
     * @param refKeyTitles 
     * @deprecated misuse could cause unexpected errors, not recommended for
     * use. Use addRefKey() instead.
     */
    @Deprecated
    public void setRefKeys(MyTable[] refKeyTables, String[][] refKeyTitles) {
        this.refKeyTables = refKeyTables;
        this.refKeyTitles = refKeyTitles;
    }
    
    /**
     * Marks a column as a reference key.
     * @param colTitle The column to mark.
     * @param refKeyTable The table the column references.
     * @param refKeyTitles The columns in the reference table used for 
     * display purposes.
     */
    public void addRefKey(String colTitle, MyTable refKeyTable, String... refKeyTitles) {
        int colIndex = t.getColIdxByName(colTitle);
        if(colIndex == -1)
                throw new RuntimeException("Cannot find title with name: "+colTitle);
        this.refKeyTables[colIndex] = refKeyTable;
        this.refKeyTitles[colIndex] = refKeyTitles;
    }
    
    /**
     * Sets whether this frame is visible. Similar to JFrame.setVisible().
     * @param visible If true, the frame becomes visible.
     * @deprecated Limited functionality. Use openViewer() instead.
     */
    @Deprecated @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
    
    
    /**
     * Populates the frame with the required elements.
     * @param editable Whether the frame can edit the table.
     */
    private void populateFrame(boolean editable) {
        setTitle("Table: \""+t.getTableName()+"\"");
        mainPanel = new JPanel(new BorderLayout());
            model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                   return false;
                }
            };
            table = new JTable(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            
            JPanel toolbarPanel = new JPanel(new BorderLayout());
            toolbarPanel.add(getQuickOptionPanel(editable), BorderLayout.WEST);
            mainPanel.add(toolbarPanel, BorderLayout.NORTH);
        this.add(mainPanel);
        
        t2Table();
    }
    
    /**
     * Gets the editing panel.
     * @param editable Whether the frame can edit the table.
     * @return The panel of buttons.
     */
    private JPanel getQuickOptionPanel(boolean editable) {
        JPanel out = new JPanel();
        if(myParent!=null) {
            JButton backB = new JButton("Back");
            backB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispatchEvent(new WindowEvent(thisFrame, WindowEvent.WINDOW_CLOSING));
                }
            });
            out.add(backB);
        }
        if(editable) {
            JButton addB = new JButton(getIcon("/add-16.png"));
            addB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addRow();
                }
            });
            out.add(addB);
            
            JButton editB = new JButton(getIcon("/edit-16.png"));
            editB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editRow();
                }
            });
            out.add(editB);
            
            JButton deleteB = new JButton(getIcon("/delete-16.png"));
            deleteB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeRow();
                }
            });
            out.add(deleteB);
        }
            JButton filterB = new JButton("fx");
            filterB.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showFunctionMenu();
                }
            });
            out.add(filterB);
            
            JTextField tField = new JTextField(10);
            tField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {}

                @Override
                public void keyReleased(KeyEvent e) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)"+tField.getText()));
                    table.setRowSorter(sorter);
                }
            });
            out.add(tField);
        return out;
    }
    
    /**
     * Load an ImageIcon from disk.
     * @param iconURL The resource location.
     * @return The loaded ImageIcon.
     */
    private Icon getIcon(String iconURL) {
        URL url = this.getClass().getResource(iconURL);
        return new ImageIcon(url);
    }
    
    /**
     * Shows a list of functions that can be applied on the open table.
     */
    private void showFunctionMenu() {
        JPanel p = new JPanel(new GridLayout(0, 1));
        ArrayList<JComboBox> boxes = new ArrayList<>();
        
        GetterFrame gf = new GetterFrame(this, p, "Group By");
        
        ActionListener changeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean allFull = true;
                for(JComboBox box : boxes) {
                    String text = (String)box.getSelectedItem();
                    if(text.length() == 0) {
                        allFull = false;
                        break;
                    }
                }
                if(allFull) {
                    p.add(getnewComboBoxPair(boxes, this));
                }
                p.repaint();
                p.validate();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gf.repaint();
                    }
                });
            }
        };
        
        JComboBox title2GroupBox = new JComboBox(MyTable.insert(t.getColTitles(), "", 0));
        title2GroupBox.addActionListener(changeListener);
        boxes.add(title2GroupBox);
        p.add(title2GroupBox);
        
        gf.showAndComplete(500, 400);
        if(!gf.isInputComplete()) return;
        String title2Group = (String) title2GroupBox.getSelectedItem();
        if(title2Group.length() == 0) return;
        ArrayList<TableOperationInterface> ops = new ArrayList<>();
        for(int i=1; i<boxes.size(); i+=2) {
            String title = (String) boxes.get(i).getSelectedItem();
            String op = (String) boxes.get(i+1).getSelectedItem();
            if(title.length()==0 || op.length()==0) continue;
            switch(op) {
                case "Sum": 
                    ops.add(t.getSumOp(title));
                    break;
                case "Average": 
                    ops.add(t.getAvgOp(title));
                    break;
                case "Unique": 
                    ops.add(t.getUniqueOp(title));
                    break;
            }
        }
        MyTable newTable = (MyTable) t.runOp(false, -1, t.getGroupOp("TEMP", title2Group, ops));
        MyTableViewer newViewer = new MyTableViewer(newTable, false, this);
        newViewer.openViewer();
    }
    private JPanel getnewComboBoxPair(ArrayList<JComboBox> boxes, ActionListener changeListener) {
        JPanel out = new JPanel();
        JComboBox box = new JComboBox(MyTable.insert(t.getColTitles(), "", 0));
        box.addActionListener(changeListener);
        boxes.add(box);
        out.add(box);
        box = new JComboBox(new String[] {"","Sum", "Average", "Unique"});
        box.addActionListener(changeListener);
        boxes.add(box);
        out.add(box);
        return out;
    }
    
    /**
     * Opens this table viewer to the specified size, and centered within the
     * screen.
     * @param witdh The frame width.
     * @param height The frame height.
     */
    public void openViewer(int witdh, int height) {
        if(myParent != null)
            myParent.setVisible(false);
        this.setSize(witdh, height);
        this.setColWidth(0, 1);
        this.setVisible(true);
    }
    
    /**
     * Opens this table viewer with a size of 500x400, and centered within the
     * screen.
     */
    public void openViewer() {
        openViewer(500, 400);
    }
    
    /**
     * Set the preferred width of a column.
     * @param colIdx The index of the column to resize.
     * @param w The preferred width.
     */
    public void setColWidth(int colIdx, int w) {
        table.getColumnModel().getColumn(colIdx).setPreferredWidth(w);
    }
    
    /**
     * Set the preferred width of a column.
     * @param colTitle The name of the column to resize.
     * @param w The preferred width.
     */
    public void setColWidth(String colTitle, int w) {
        setColWidth(t.getColIdxByName(colTitle), w);
    }
    
    
    /**
     * Shows a dialog for adding a new row.
     */
    public void addRow() {
        String[] newRow = showDialog("Add Record", null);
        if(newRow == null) return;
        String pk = t.addRow(newRow);
        model.addRow(MyTable.insert(newRow, pk, 0));
    }
    
    /**
     * Shows a dialog for editing a row.
     */
    public void editRow() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        
        String pk = (String) model.getValueAt(row, 0);
        String[] oldRow = t.getRow(pk);
        String[] newRow = showDialog("Edit Record", oldRow);
        if(newRow == null) return;
        
        t.setRow(pk, newRow);
        newRow = t.parseRow(newRow);
        setModelRow(row, 1, newRow);
    }
    
    /**
     * Sets the row in the table model, without changing the first n columns.
     * @param rowIndex The index of the row in the table model to change.
     * @param colStart The column to start changing the values in.
     * @param values The new values to set the road to.
     */
    private void setModelRow(int rowIndex, int colStart, String... values) {
        for(int i=0; i<values.length; i++) {
            model.setValueAt(values[i], rowIndex, colStart+i);
        }
    }
    
    /**
     * Removes a row from the table. Caution: this cannot be undone.
     */
    public void removeRow() {
        int[] selRows = table.getSelectedRows();
        for(int rowIndex : selRows) {
            String pk = (String) model.getValueAt(rowIndex, 0);
            t.removeRow(pk);
            model.removeRow(rowIndex);
        }
    }
    
    
    /**
     * Updates the JTable with the values in the MyTable object.
     */
    private void t2Table() {
        model.setColumnIdentifiers(MyTable.insert(t.getColTitles(), "ID", 0));
        for(Map.Entry<String, String[]> entry : t.getRows().entrySet()) {
            String key = entry.getKey();
            String[] value = t.getParsedRow(key);
            model.addRow(MyTable.insert(value, key, 0));
        }
    }
    
    
    /**
     * Shows a dialog to edit/add a row.
     * @param dialogTitle The title of the dialog.
     * @param preValues The initial values of the TextFields.
     * @return The new values for the selected row.
     */
    public String[] showDialog(String dialogTitle, String[] preValues) {
        GetterFrame gf = new GetterFrame(this, dialogTitle);
        JComponent[] fields = new JComponent[t.getColTitles().length];
        for(int i=0; i<fields.length; i++) {
            MyTable currTable = refKeyTables[i];
            if(currTable == null) {
                fields[i] = gf.addTextField(t.getColTitles()[i]);
                if(preValues != null)
                    ((JTextField)fields[i]).setText(preValues[i]);
                else if(t.getDefaultValue(i) != null) {
                    ((JTextField)fields[i]).setText(t.getDefaultValue(i));
                }
            } else {
                MyTable table = currTable.selectColumns("tmp", -1, refKeyTitles[i]);
                table = table.catColumns("tmp", "\u2000", refKeyTitles[i]);
                HashMap<String, String> col = table.getColumn("tmp", true);
                TreeSet<String> set = new TreeSet<>(col.values());
                String[] options = set.toArray(new String[set.size()]);
                //Arrays.sort(options);
                fields[i] = gf.addComboField(t.getColTitles()[i], options, false);
                if(preValues != null)
                    ((JComboBox)fields[i]).setSelectedItem(table.getCellValue("tmp", preValues[i]));
            }
        }
        gf.showAndComplete(500, fields.length*21+110);
        if(!gf.isInputComplete()) {
            return null;
        }
        
        String[] row = new String[fields.length];
        for(int i=0; i<fields.length; i++) {
            if(fields[i] instanceof JTextField) {
                row[i] = ((JTextField)fields[i]).getText();
            } else if(fields[i] instanceof JComboBox) {
                String tempVal = (String) ((JComboBox)fields[i]).getSelectedItem();
                if(tempVal == null) return null;
                row[i] = refKeyTables[i].getFirstRowByValues(refKeyTitles[i], tempVal.split("\u2000"));
            }
        }
        
        return row;
    }
    
}
