
package databasepackage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import table.operations.*;

/**
 * The MyTable class is a relational database toolkit intended for light weight
 * use cases such as prototype or personal projects, where a full DBMS may be
 * unnecessary. This implementation is fully built using java making it highly
 * portable.
 * @author Saud Fatayerji
 */
public class MyTable {
    
    private static ScriptEngineManager mgr;
    private static ScriptEngine engine;
    
    private String name;
    private String[] titles;
    private String[] defaultValues;
    private int nextPk = 0;
    private HashMap<String, String[]> content;

    /**
     * Creates a new table
     * @param name  The name/title of the new table.
     * @param titles The array of column titles the new table should contain.
     * @param content An ArrayList of rows to add to the table.
     */
    public MyTable(String name, String[] titles, ArrayList<String[]> content) {
        this.name = name;
        this.titles = titles;
        this.defaultValues = new String[titles.length];
        this.content = new HashMap<>();
        for(int i=0; i<content.size(); i++) {
            this.content.put(""+i, content.get(i));
        }
        nextPk = this.content.size();
        
        if(mgr == null)
            mgr = new ScriptEngineManager();
        if(engine == null)
            engine = mgr.getEngineByName("JavaScript");
    }

    /**
     * Creates a new table
     * @param name  The name/title of the new table.
     * @param titles The array of column titles the new table should contain.
     */
    public MyTable(String name, String... titles) {
        this(name, titles, new ArrayList<>());
    }

    /**
     * Constructs a new Table with the same titles and content as the specified 
     * table.
     * @param table The table for which the contents are cloned.
     */
    public MyTable(MyTable table) {
        name = table.name;
        titles = table.titles.clone();
        defaultValues = table.defaultValues.clone();
        nextPk = table.nextPk;
        content = new HashMap<>();
        for(Map.Entry<String, String[]> entry : table.content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            content.put(key, value.clone());
        }
    }
    
    
    /**
     * Gets the name/title of the table.
     * @return The name/title of the table.
     */
    public String getTableName() {
        return name;
    }
    
    /**
     * Sets the name/title of the table.
     * @param name The new name/title of the table.
     */
    public void setTableName(String name) {
        this.name = name;
    }
    
    
    /**
     * Gets the column titles of this table.
     * @return An array of the column titles of this table.
     */
    public String[] getColTitles() {
        return titles;
    }
    
    /**
     * Gets the column title at the given index of the columns in this table.
     * @param pos The index of the title to get.
     * @return The column title at the given index of the columns in this table.
     */
    public String getColTitle(int pos) {
        return titles[pos];
    }
    
    /**
     * Sets the column title at the given index of the columns in this table.
     * @param pos The index of the title to get.
     * @param newTitle The new title for the column.
     */
    public void setColTitle(int pos, String newTitle) {
        this.titles[pos] = newTitle;
    }
    
    /**
     * Sets the column title at the given index of the columns in this table.
     * @param oldTitle The title for the column to rename.
     * @param newTitle The new title for the column.
     */
    public void setColTitle(String oldTitle, String newTitle) {
        setColTitle(getColIdxByName(oldTitle), newTitle);
    }
    
    
    /**
     * Gets the default value for the selected column. 
     * When a new row is added and the value for a column with a default value is
     * set to null, the null value  gets replaced by the default value.
     * @param colTitle The column title.
     * @return The default value for the selected column.
     */
    public String getDefaultValue(String colTitle) {
        int colIndex = getColIdxByName(colTitle);
        return getDefaultValue(colIndex);
    }
    
    /**
     * Gets the default value for the selected column. 
     * When a new row is added and the value for a column with a default value is
     * set to null, the null value  gets replaced by the default value.
     * @param colIndex The column index.
     * @return The default value for the selected column.
     */
    public String getDefaultValue(int colIndex) {
        return defaultValues[colIndex];
    }
    
    /**
     * Gets the default value for the selected column. 
     * When a new row is added and the value for a column with a default value is
     * set to null, the null value  gets replaced by the default value.
     * @param colTitle The column title.
     * @param defaultValue The new default value for the selected column.
     */
    public void setDefaultValue(String colTitle, String defaultValue) {
        int colIdx = getColIdxByName(colTitle);
        setDefaultValue(colIdx, defaultValue);
    }
    
    /**
     * Gets the default value for the selected column. 
     * When a new row is added and the value for a column with a default value is
     * set to null, the null value  gets replaced by the default value.
     * @param colIdx The column index.
     * @param defaultValue The new default value for the selected column.
     */
    public void setDefaultValue(int colIdx, String defaultValue) {
        this.defaultValues[colIdx] = defaultValue;
    }
    
    
    /**
     * Gets the value stored in the cell at the specified column and row.
     * @param colIdx The index of the column.
     * @param recordId The primary key of the row.
     * @return The value in the specified cell.
     */
    public String getCellValue(int colIdx, String recordId) {
        return getRow(recordId)[colIdx];
    }
    
    /**
     * Gets the value stored in the cell at the specified column and row.
     * @param colTitle The title of the column.
     * @param recordId The primary key of the row.
     * @return The value in the specified cell.
     */
    public String getCellValue(String colTitle, String recordId) {
        int colIdx = getColIdxByName(colTitle);
        return getCellValue(colIdx, recordId);
    }
    
    
    /**
     * Gets the number of rows in the table.
     * @return The number of rows in the table.
     */
    public int getRowCount() {
        return content.size();
    }
    
    /**
     * Check if there are no rows in this table.
     * @return true if there are no rows in this table, otherwise false.
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    /**
     * Adds a row to the table. The primary key is automatically generated.
     * @param row The list of values to add. Any null elements will be replaced
     * with the default value provided using the setDefaultValue method.
     * @return The new primary key of the added row.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    public String addRow(String... row) {
        if(row.length != titles.length)
            throw new RuntimeException("Expected row length ("+row.length+") does not match column count ("+titles.length+")");
        for(int i=0; i<row.length; i++) {
            if(row[i] == null) row[i] = getDefaultValue(i);
        }
        content.put(nextPk+"", row);
        return ""+(nextPk++);
    }
    
    /**
     * Sets new values for the given row.
     * @param recordId The primary key of the row to modify.
     * @param row The list of values to add. Any null elements will be replaced
     * with the default value provided using the setDefaultValue method.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    public void setRow(String recordId, String... row) {
        if(row.length != titles.length)
            throw new RuntimeException("Expected row length ("+row.length+") does not match column count ("+titles.length+")");
        for(int i=0; i<row.length; i++) {
            if(row[i] == null) row[i] = getDefaultValue(i);
        }
        content.put(recordId, row);
    }
    
    /**
     * Gets the entire set of values for the given row. Note: any changes made to the returned
     * value directly affect the MyTable object.
     * @param recordId The primary key of the row to get.
     * @return The set of values for the given row.
     */
    public String[] getRow(String recordId) {
        return content.get(recordId);
    }
    
    /**
     * Gets the content of the table. Note: any changes made to the returned
     * value directly affect the MyTable object.
     * @return 
     */
    public HashMap<String, String[]> getRows() {
        return content;
    }
    
    /**
     * Remove a row from the table. Note: any deletions from this table will not
     * cascade to other tables.
     * @param recordId The primary key of the row to delete.
     */
    public void removeRow(String recordId) {
        content.remove(recordId);
    }
    
    /**
     * Removes multiple rows from the table. Note: any deletions from this table
     * will not cascade to other tables.
     * @param recordIds The primary keys of the rows to delete.
     */
    public void removeRows(String... recordIds) {
        for (String recordId : recordIds) {
            removeRow(recordId);
        }
    }
    
    /**
     * Gets the first row that contains a value in the specified column.
     * @param colTitle The column to check.
     * @param searchValue The value to check for.
     * @return The primary key of the row found.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getFilterOp() instead.
     */
    @Deprecated
    public String getFirstRowByValue(String colTitle, String searchValue) {
        int colIndex = getColIdxByName(colTitle);
        
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if(parseCell(colIndex, value).equals(searchValue)) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * Gets the first row that contains all of the specified values in the
     * specified columns.
     * @param colTitles The columns to check.
     * @param searchValues The values to check for.
     * @return The primary key of the row found.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getFilterOp() instead.
     */
    @Deprecated
    public String getFirstRowByValues(String[] colTitles, String[] searchValues) {
        int[] colIndexes = new int[colTitles.length];
        for(int i=0; i<colTitles.length; i++) {
            colIndexes[i] = getColIdxByName(colTitles[i]);
        }
        
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            boolean allTrue = true;
            for(int i=0; i<colIndexes.length; i++) {
                if(!parseCell(colIndexes[i], value).equals(searchValues[i])) {
                    allTrue = false;
                    break;
                }
            }
            if(allTrue) return key;
        }
        return null;
    }
    
    /**
     * Gets the rows that contain the specified value in the specified column.
     * @param colTitle The column to check.
     * @param searchValue The value to check for.
     * @return The primary keys of the rows found.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getFilterOp() instead.
     */
    @Deprecated
    public String[] getRowsByValue(String colTitle, String searchValue) {
        int colIndex = getColIdxByName(colTitle);
        
        ArrayList<String> out = new ArrayList();
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if(parseCell(colIndex, value).equals(searchValue)) {
                out.add(key);
            }
        }
        return out.toArray(new String[out.size()]);
    }
    
    
    /**
     * Gets the number of columns in the table.
     * @return The number of columns in the table.
     */
    public int getColCount() {
        return titles.length;
    }
    
    /**
     * Gets the index of the specified column.
     * @param colTitle the name of the specified column.
     * @return The index of the specified column.
     */
    public int getColIdxByName(String colTitle) {
        for(int i=0; i<titles.length; i++) {
            if(colTitle.equals(titles[i]))
                return i;
        }
        throw new RuntimeException("Cannot find title with name: "+colTitle);
    }
    
    /**
     * Gets the indexes of the specified columns.
     * @param colTitles the name of the specified columns.
     * @return The indexes of the specified column.
     */
    public int[] getColIdxsByNames(String... colTitles) {
        int[] colIndexes = new int[colTitles.length];
        for(int i=0; i<colTitles.length; i++) {
            colIndexes[i] = getColIdxByName(colTitles[i]);
        }
        return colIndexes;
    }
    
    /**
     * The join operation changes the column names to the format
     * "Table_name.column_name". This method strips away any table names leaving
     * only the column names
     */
    public void trimColumnNames() {
        for(int i=0; i<titles.length; i++) {
            titles[i] = titles[i].substring(titles[i].lastIndexOf(".")+1);
        }
    } 
    
    /**
     * Creates a new table with only the specified columns found in this table.
     * This method can also be used to change the order of the columns.
     * @param newTableName The name for the new table.
     * @param threadCount The number of threads to use for this operation. Set
     * this to -1 to set the number automatically.
     * @param colTitles The titles of the columns to select.
     * @return The newly created table.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getSelectColsOp() instead.
     */
    @Deprecated
    public MyTable selectColumns(String newTableName, int threadCount, String... colTitles) {
        return (MyTable) runOp(false, threadCount, getSelectColsOp(newTableName, colTitles));
    }
    
    /**
     * Gets the specified column as a HashMap.
     * @param colTitle The column title.
     * @param parse If true, any functions in the cells will be parsed before
     * being returned. Note: refer to parseRow() for more information on parsing.
     * @return returns the values in the specified column as a HashMap where the
     * key is the row primary_key and the values is the cell value.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getSelectColsOp() instead.
     */
    @Deprecated
    public HashMap<String, String> getColumn(String colTitle, boolean parse) {
        HashMap<String, String> out = new HashMap<>();
        int colIndex = getColIdxByName(colTitle);
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            String outStr = (parse) ? parseCell(colIndex, value) : value[colIndex];
            out.put(key, outStr);
        }
        return out;
    }
    
    /**
     * Adds a new column to the end of this table. The column will be filled
     * with the specified default value.
     * @param name The title of the column to add.
     * @param defaultValue The value that the column will be filled with.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getInsertColsOp() instead.
     */
    @Deprecated
    public void appendColumn(String name, String defaultValue) {
        insertColumn(name, -1, defaultValue);
    }
    
    /**
     * Inserts a new column into specified position of this table. The column
     * will be filled with the specified default value.
     * @param name The title of the column to add.
     * @param position The index of the column to insert at. Similar to
     * ArrayList.add(int index, E element).
     * @param defaultValue The value that the column will be filled with.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getInsertColsOp() instead.
     */
    @Deprecated
    public void insertColumn(String name, int position, String defaultValue) {
        titles = insert(titles, name, position);
        defaultValues = insert(defaultValues, null, position);
        if(content.isEmpty()) return;
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            content.put(key, insert(value, defaultValue, position));
        }
    }
    
    /**
     * Adds a new column to the end of this table. The column will be filled
     * with the specified values in the provided column.
     * @param newColname The title of the column to add.
     * @param column The values that the column will be filled with. The KeySet
     * in this parameter must equal the MeySet of the table content.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getInsertColsOp() instead.
     */
    @Deprecated
    public void addColumn(String newColname, HashMap<String, String> column) {
        titles = insert(titles, newColname, -1);
        defaultValues = insert(defaultValues, null, -1);
        if(content.isEmpty()) return;
        for (Map.Entry<String, String> entry : column.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            content.put(key, insert(content.get(key), value, -1));
        }
    }
    
    /**
     * Removes a specified column from this table.
     * @param colTitle The title of the column to remove.
     * @deprecated This method does not support multi-threading and is very
     * inefficient. It will be removed in a later version. Use getRemoveColsOp()
     * instead.
     */
    @Deprecated
    public void removeColumn(String colTitle) {
        int colIndex = getColIdxByName(colTitle);
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            content.put(key, removeAt(value, colIndex));
        }
        titles = removeAt(titles, colIndex);
        defaultValues = removeAt(defaultValues, colIndex);
    }
    
    /**
     * Removes the specified columns from this table.
     * @param colTitles The titles of the columns to remove.
     * @deprecated This method does not support multi-threading and is extremely
     * inefficient. It will be removed in a later version. Use getRemoveColsOp()
     * instead.
     */
    @Deprecated
    public void removeColumns(String... colTitles) {
        for(String name : colTitles) {
            removeColumn(name);
        }
    }
    
    /**
     * Concatenate the values of multiple columns. The new column is added to
     * the end of a new table.
     * @param newColTitle The title of the column that will be created.
     * @param separator The separator used when concatenating the values in the
     * columns.
     * @param colNames The titles of the columns to cat. The order provided here
     * will match the order that the values are joined in. These will be removed
     * from the resulting table.
     * @return The new table.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getInsertColsOp() instead.
     */
    @Deprecated
    public MyTable catColumns(String newColTitle, String separator, String... colNames) {
        MyTable out = new MyTable(this);
        HashMap<String, String>[] cols = new HashMap[colNames.length];
        for(int i=0; i<colNames.length; i++) {
            cols[i] = getColumn(colNames[i], true);
        }
        
        HashMap<String, String> newCol = new HashMap<>();
        for (Map.Entry<String, String> entry : cols[0].entrySet()) {
            String key = entry.getKey();
            String newCell = "";
            for(int i=0; i<colNames.length; i++) {
                newCell += cols[i].get(key) + separator;
            }
            newCol.put(key, newCell.substring(0, newCell.length()-separator.length()));
        }
        
        out.addColumn(newColTitle, newCol);
        
        for(String name : colNames) {
            out.removeColumn(name);
        }
        return out;
    }
    
    /**
     * Creates a new table containing only rows approved by the filter.
     * @param f The filter to use.
     * @return The new table with the filtered rows.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getInsertColsOp() instead.
     */
    @Deprecated
    public MyTable filterRows(Filter<String, String[]> f) {
        MyTable out = new MyTable(this);
        ArrayList<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : out.content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if(!f.shouldKeep(key, value))
                keysToRemove.add(key);
        }
        for(String key : keysToRemove)
            out.removeRow(key);
        return out;
    }
    
    /**
     * Creates a new table containing only rows where the one of the specified
     * values was found in the specified column.
     * @param colTitle The title of the column to check.
     * @param values The values to check for.
     * @return The new table with the filtered rows.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getFilterOp() instead.
     */
    @Deprecated
    public MyTable filterRows(String colTitle, HashSet<String> values) {
        int colIndex = getColIdxByName(colTitle);
        return filterRows(new Filter<String, String[]>() {
            @Override
            public boolean shouldKeep(String k, String[] v) {
                String[] value = v;
                return values.contains(value[colIndex]);
            }
        });
    }
    
    /**
     * Creates a new table containing only rows where the one of the specified
     * values was found in the specified column.
     * @param colTitle The title of the column to check.
     * @param values The values to check for.
     * @return The new table with the filtered rows.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use getFilterOp() instead.
     */
    @Deprecated
    public MyTable filterRows(String colTitle, String... values) {
        return filterRows(colTitle, new HashSet<>(Arrays.asList(values)));
    }
    
    
    /**
     * Joins together tables on a (reference) column.
     * @param newTableName The name for the new table.
     * @param refColName The title of the (reference) column.
     * @param table The table to join to (contains the primary keys).
     * @return The new table.
     * @deprecated This method does not support multi-threading and will be
     * removed in a later version. Use __________________________________________() instead.
     */
    @Deprecated
    public MyTable join(String newTableName, String refColName, MyTable table) {
        ArrayList<String> newTitles = new ArrayList<>();
        for(int i=0; i<titles.length; i++) {
            newTitles.add(name+"."+titles[i]);
        }
        for(int i=0; i<table.titles.length; i++) {
            newTitles.add(table.name+"."+table.titles[i]);
        }
        MyTable out = new MyTable(newTableName, newTitles.toArray(new String[newTitles.size()]));
        
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String[] value = entry.getValue();
            
            String[] newRow = new String[out.titles.length];
            String[] refRow = null;
            int refColIndex = getColIdxByName(refColName);
            for(int j=0; j<newRow.length; j++) {
                if(j<titles.length) {
                    newRow[j] = value[j];
                    if(refColIndex == j)
                        refRow = table.getRow(newRow[j]);
                } else if(refRow != null) {
                    newRow[j] = refRow[j-titles.length];
                }
            }
            out.setRow(entry.getKey(), newRow);
        }
        out.removeColumn(name+"."+refColName);
        return out;
    } /////////////
    
    /*public MyTable join2(String newTableName, String refColName, MyTable table) {
        
    }*/
    
    
    /**
     * Gets a new FilterOp.
     * @param f The filter to use.
     * @return The new new FilterOp. The FilterOp.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public FilterOp getFilterOp(Filter<String, String[]> f) {
        FilterOp out = new FilterOp(getTableName(), getColTitles(), f);
        return out;
    }
    
    /**
     * Gets a new FilterOp. The FilterOp searches for rows where each specified
     * column contains a value found in the respective set of values.
     * @param colTitles The titles of the columns to check.
     * @param values The values to check for. Each set of the values array
     * corresponds to the title in the same position in the colTitles array.
     * @return The new new FilterOp. The FilterOp.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public FilterOp getFilterOp(String[] colTitles, HashSet<String>[] values) {
        return null;
    }
    
    /**
     * Gets a new FilterOp. The FilterOp searches for rows where the specified
     * column contains a value found in the provided set of values.
     * @param colTitle The title of the column to check.
     * @param values The values to check for. 
     * @return The new new FilterOp. The FilterOp.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public FilterOp getFilterOp(String colTitle, HashSet<String> values) {
        int colIndex = getColIdxByName(colTitle);
        return getFilterOp(new Filter<String, String[]>() {
            @Override
            public boolean shouldKeep(String k, String[] v) {
                return values.contains(v[colIndex]);
            }
        });
    }
    
    /**
     * Gets a new FilterOp. The FilterOp searches for rows where the specified
     * column contains a value found in the provided set of values.
     * @param colTitle The title of the column to check.
     * @param values The values to check for.
     * @return The new new FilterOp. The FilterOp.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public FilterOp getFilterOp(String colTitle, String... values) {
        return getFilterOp(colTitle, new HashSet<>(Arrays.asList(values)));
    }
    
    /**
     * Gets a new CatColumnsOp. Concatenates the values of multiple columns.
     * The new column is added to the end of a new table.
     * @param newTableName The name for the new table.
     * @param newColTitle The title of the column that will be created.
     * @param separator The separator used when concatenating the values in the
     * columns.
     * @param colsToJoin The titles of the columns to join. The order provided
     * here will match the order that the values are joined in. These will be
     * removed from the resulting table.
     * @return The new new CatColumnsOp. The CatColumnsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public CatColumnsOp getCatColumnsOp(String newTableName, String newColTitle, String separator, String... colsToJoin) {
        CatColumnsOp out = new CatColumnsOp(newTableName, newColTitle, separator, titles, getColIdxsByNames(colsToJoin));
        return out;
    }
    
    /**
     * Inserts a new column into specified position of a new table. The column
     * will be filled with the specified default value.
     * @param newTableName The name for the new table.
     * @param newColTitle The title of the column that will be created.
     * @param position The index of the column to insert at. Similar to
     * ArrayList.add(int index, E element).
     * @param defaultValue The value that the column will be filled with.
     * @return The new new InsertColsOp. The InsertColsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public InsertColsOp getInsertColsOp(String newTableName, int position, String newColTitle, String defaultValue) {
        InsertColsOp out = new InsertColsOp(getColTitles(), newTableName, position, newColTitle, defaultValue);
        return out;
    }
    
    /**
     * Inserts new columns into the specified positions of a new table. The
     * columns will be filled with the specified default values.
     * @param newTableName The name for the new table.
     * @param positions The indexes of the columns to insert at. Similar to
     * ArrayList.add(int index, E element).
     * @param newColTitles The titles of the columns that will be created.
     * @param defaultValues The values that the columns will be filled with.
     * @return The new new InsertColsOp. The InsertColsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public InsertColsOp getInsertColsOp(String newTableName, int[] positions, String[] newColTitles, String[] defaultValues) {
        InsertColsOp out = new InsertColsOp(getColTitles(), newTableName, positions, newColTitles, defaultValues);
        return out;
    }
    
    /**
     * Inserts new columns into the specified positions of a new table. The
     * columns will be filled with the columns found in the provided table.
     * @param newTableName The name for the new table.
     * @param positions The indexes of the columns to insert at. Similar to
     * ArrayList.add(int index, E element).
     * @param columns A MyTable of the columns to insert.
     * @return The new new InsertColsOp. The InsertColsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public InsertColsOp getInsertColsOp(String newTableName, int[] positions, MyTable columns) {
        InsertColsOp out = new InsertColsOp(getColTitles(), newTableName, positions, columns);
        return out;
    }
    
    /**
     * Creates a new table with only the specified columns found in this table.
     * This method can also be used to change the order of the columns.
     * @param newTableName The name for the new table.
     * @param colTitles An array of the columns to select.
     * @return The new new SelectColsOp. The SelectColsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public SelectColsOp getSelectColsOp(String newTableName, String... colTitles) {
        SelectColsOp out = new SelectColsOp(newTableName, colTitles, getColIdxsByNames(colTitles));
        return out;
    }
    
    /**
     * Creates a new HashMap with only the values found in the specified column.
     * Each key value pair of the map contains the row primary key and cell
     * value respectively.
     * @param colTitle The title of the column to select.
     * @return The new new SelectColsOp. The SelectColsOp.getResult() method of
     * the returned value returns a HashMap object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public SelectColOp getSelectColOp(String colTitle) {
        int colIdx = getColIdxByName(colTitle);
        SelectColOp out = new SelectColOp(colTitle, colIdx);
        return out;
    }
    
    /**
     * Creates a new table excluding the specified columns found in this table.
     * @param newTableName The name for the new table.
     * @param colTitles The titles of the columns to remove.
     * @return The new new RemoveColsOp. The RemoveColsOp.getResult() method of
     * the returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public RemoveColsOp getRemoveColsOp(String newTableName, String... colTitles) {
        RemoveColsOp out = new RemoveColsOp(getColTitles(), newTableName, colTitles);
        return out;
    }
    
    /**
     * Gets all of the unique variables found in the specified column.
     * @param colTitle The title of the column to operate on.
     * @return The new new UniqueOp. The UniqueOp.getResult() method of
     * the returned value returns a HashSet of Strings. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public UniqueOp getUniqueOp(String colTitle) {
        int colIndex = getColIdxByName(colTitle);
        UniqueOp out = new UniqueOp(colTitle, colIndex);
        return out;
    }
    
    /**
     * Gets the sum of the variables found in the specified column. Any
     * non-numeric cells are ignored.
     * @param colTitle The title of the column to operate on.
     * @return The new new SumOp. The SumOp.getResult() method of
     * the returned value returns a Double object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public SumOp getSumOp(String colTitle) {
        int colIndex = getColIdxByName(colTitle);
        SumOp out = new SumOp(colTitle, colIndex);
        return out;
    }
    
    /**
     * Gets the mean of the variables found in the specified column. Any
     * non-numeric cells are ignored.
     * @param colTitle The title of the column to operate on.
     * @return The new new AvgMeanOp. The AvgMeanOp.getResult() method of 
     * the returned value returns a Double object. The results of the returned 
     * operation must be calculated using the runOp() method.
     */
    public AvgMeanOp getAvgOp(String colTitle) {
        int colIndex = getColIdxByName(colTitle);
        AvgMeanOp out = new AvgMeanOp(colTitle, colIndex);
        return out;
    }
    
    /**
     * Joins two tables together. Rows are matched according to the primary key
     *  - reference key relationship.
     * @param newTableName The name for the new table.
     * @param refColTitle The title of the reference column in the reference table.
     * @param pkTable The table containing the primary key.
     * @return The new new AvgMeanOp. The AvgMeanOp.getResult() method of 
     * the returned value returns a Double object. The results of the returned 
     * operation must be calculated using the runOp() method.
     */
    public JoinTableOp getJoinTableOp(String newTableName, String refColTitle, MyTable pkTable) {
        JoinTableOp out = new JoinTableOp(newTableName, refColTitle, pkTable, this);
        return out;
    }
    
    /**
     * Creates a new table where repeated elements in the specified column are
     * grouped together. The provided ops are then run separately for each of
     * the grouped rows. Similar to the SQL 'GROUP BY' statement.
     * @param newTableName The name for the new table.
     * @param titleToGroup The title of the column to group.
     * @param ops The operations to run on the grouped records.
     * @return The new new GroupOps. The GroupOps.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public GroupOps getGroupOp(String newTableName, String titleToGroup, TableOperationInterface... ops) {
        GroupOps out = new GroupOps(newTableName, titleToGroup, getColIdxByName(titleToGroup), ops);
        return out;
        
        /*int colIndex = getColIdxByName(titleToGroup);
        
        String[] newTitles = new String[ops.length+1];
        newTitles[0] = titleToGroup;
        for(int i=1; i<newTitles.length; i++) {
            newTitles[i] = ops[i-1].title;
        }
        int[] colIndexes = new int[ops.length];
        for(int i=1; i<colIndexes.length; i++) {
            colIndexes[i] = getColIdxByName(ops[i].title);
        }
        
        ReductionOperation op = new ReductionOperation(null, -1) {
            HashMap<String, ReductionOperation[]> map = new HashMap<>();
            
            @Override
            public void runOp(String key, String[] row) {
                if(!map.containsKey(row[colIndex]))
                    map.put(row[colIndex], ReductionOperation.cloneOpsArr(ops));
                for(ReductionOperation op : map.get(row[colIndex]))
                    op.runOp(key, row);
            }
            
            @Override
            public Object getResult() {
                MyTable table = new MyTable(newTableName, newTitles);
                for(Map.Entry<String, ReductionOperation[]> entry : map.entrySet()) {
                    String key = entry.getKey();
                    ReductionOperation[] value = entry.getValue();
                    table.addRow(insert(ReductionOperation.getOpsResultsAsStr(value), key, 0));
                }
                return table;
            }

            @Override
            public Object getVars() {
                return map;
            }

            @Override
            public void mergeResult(ReductionOperation op) {
                map.putAll((Map<? extends String, ? extends ReductionOperation[]>) op.getVars());
            }
        };
        return op;*/
    }
    
    /**
     * Creates a new table where repeated elements in the specified column are
     * grouped together. The provided ops are then run separately for each of
     * the grouped rows. Similar to the SQL 'GROUP BY' statement.
     * @param newTableName The name for the new table.
     * @param titleToGroup The title of the column to group.
     * @param ops The operations to run on the grouped records
     * @return The new new GroupOps. The GroupOps.getResult() method of the
     * returned value returns a MyTable object. The results of the returned
     * operation must be calculated using the runOp() method.
     */
    public GroupOps getGroupOp(String newTableName, String titleToGroup, ArrayList<TableOperationInterface> ops) {
        return getGroupOp(newTableName, titleToGroup, ops.toArray(new TableOperationInterface[ops.size()]));
    }
    
    
    /**
     * Runs all provided ops on each of the rows in the table. Can be
     * multi-threaded.
     * @param parse If the rows should be parsed before being passed to the
     * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
     * method for more information on parsing.
     * @param threadCount The number of threads to use for this operation. Set
     * this to -1 to set the number automatically.
     * @param ops The operations to run on the provided rows.
     * @return An array of all the results from the given ops as returned by the
     * TableOperationInterface.getOpsArrResults() method.
     */
    public MyTable runOpsSequentially(String newTableName, boolean parse, int threadCount, SequentialInterface... ops) {
        if(threadCount == -1)
            threadCount = Runtime.getRuntime().availableProcessors();
        //System.out.println("tc: "+threadCount);
        
        MyTable out = new MyTable(newTableName, ops[ops.length-1].getNewTitles());
        
        HashMap<String, String[]>[] rows = MPT.split(threadCount, content);
        MyTable.SeqProcThread[] procs = new SeqProcThread[threadCount];
        Thread[] threads = new Thread[threadCount];
        for(int i=0; i<threadCount; i++) {
            SequentialInterface[] temp = SequentialInterface.copyOpsArr(ops);
            MyTable.SeqProcThread p = new SeqProcThread(parse, rows[i], out, temp);
            Thread t = new Thread(p);
            procs[i] = p;
            threads[i] = t;
            t.start();
        }
        for(int i=0; i<procs.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {}
            out.getRows().putAll(procs[i].getTable().getRows());
        }
        return out;
    }
    
    /**
     * Runs all provided ops on each of the rows in the table. Can be
     * multi-threaded.
     * @param parse If the rows should be parsed before being passed to the
     * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
     * method for more information on parsing.
     * @param threadCount The number of threads to use for this operation. Set
     * this to -1 to set the number automatically.
     * @param ops The operations to run on the provided rows.
     * @return An array of all the results from the given ops as returned by the
     * TableOperationInterface.getOpsArrResults() method.
     */
    public Object[] runOps(boolean parse, int threadCount, TableOperationInterface... ops) {
        if(threadCount == -1)
            threadCount = Runtime.getRuntime().availableProcessors();
        //System.out.println("tc: "+threadCount);
        
        HashMap<String, String[]>[] rows = MPT.split(threadCount, content);
        MyTable.ProcThread[] procs = new ProcThread[threadCount];
        Thread[] threads = new Thread[threadCount];
        for(int i=0; i<threadCount; i++) {
            TableOperationInterface[] temp = TableOperationInterface.copyOpsArr(ops);
            MyTable.ProcThread p = new ProcThread(parse, rows[i], temp);
            Thread t = new Thread(p);
            procs[i] = p;
            threads[i] = t;
            t.start();
        }
        for(int i=0; i<procs.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {}
            for(int j=0; j<ops.length; j++) {
                ops[j].mergeResultsIntoThis(procs[i].ops[j]);
            }
        }
        return TableOperationInterface.getOpsArrResults(ops);
    }
    
    /**
     * Runs all provided ops on each of the rows in the table. Runs using only
     * one thread.
     * @param parse If the rows should be parsed before being passed to the
     * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
     * method for more information on parsing.
     * @param ops The operations to run on the provided rows.
     * @return An array of all the results from the given ops as returned by the
     * TableOperationInterface.getOpsArrResults() method.
     */
    public Object[] runOps(boolean parse, TableOperationInterface... ops) {
        return runOps(parse, 1, ops);
    }
    
    /**
     * Runs the provided op on each of the rows in the table. Can be
     * multi-threaded.
     * @param parse If the rows should be parsed before being passed to the
     * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
     * method for more information on parsing.
     * @param threadCount The number of threads to use for this operation. Set
     * this to -1 to set the number automatically.
     * @param op The operation to run on the provided rows.
     * @return The result from the given op as returned by the
     * TableOperationInterface.getResult() method.
     */
    public Object runOp(boolean parse, int threadCount, TableOperationInterface op) {
        Object[] out = runOps(parse, threadCount, op);
        return out[0];
    }
    
    /**
     * Runs the provided op on each of the rows in the table. Runs using only
     * one thread.
     * @param parse If the rows should be parsed before being passed to the
     * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
     * method for more information on parsing.
     * @param op The operation to run on the provided rows.
     * @return The result from the given op as returned by the
     * TableOperationInterface.getResult() method.
     */
    public Object runOp(boolean parse, TableOperationInterface op) {
        return runOp(parse, 1, op);
    }
    
    
    /**
     * Saves all of the provided tables into the specified file. The tables are
     * stored in XML format.
     * @param f The file in which to save the data.
     * @param tables The tables to be saved.
     */
    public static void saveTables(File f, MyTable... tables) {
        try {
            Element root = new Element("root");
                Element tablesEle = new Element("tables");
                for(MyTable t : tables) {
                    Element tableEle = new Element("table");
                     tableEle.setAttribute("name", t.name);
                     tableEle.setAttribute("nextPk", t.nextPk+"");
                        
                        Element titlesEle = new Element("titles");
                        for(int i=0; i<t.titles.length; i++) {
                            Element titleEle = new Element("title");
                            titleEle.setText(t.titles[i]);
                            if(t.defaultValues[i]!=null)
                                titleEle.setAttribute("defaultValue", t.defaultValues[i]);
                            titlesEle.addContent(titleEle);
                        }
                        tableEle.addContent(titlesEle);
                        
                        Element rowsEle = new Element("rows");
                            for (Map.Entry<String, String[]> entry : t.content.entrySet()) {
                                String pk = entry.getKey();
                                String[] row = entry.getValue();
                                Element rowEle = new Element("row");
                                rowEle.setAttribute("pk", pk);
                                for(int i=0; i<row.length; i++) {
                                    Element cellEle = new Element("cell");
                                    cellEle.setText(row[i]);
                                    rowEle.addContent(cellEle);
                                }
                                rowsEle.addContent(rowEle);
                            }
                        tableEle.addContent(rowsEle);
                        
                    tablesEle.addContent(tableEle);
                }
                root.addContent(tablesEle);
            Document doc = new Document(root);
                    
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            FileWriter fw = new FileWriter(f);
            xmlOutput.output(doc, fw);
            fw.flush();
            fw.close();
        } catch(IOException ex) {
            Logger.getLogger(MyTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads all of the stored tables from the specified file.
     * @param f The file from which to load the data.
     * @return A HashMap of all the tables found in the specified file. The map
     * keys refer to the name of the table. If an error occurs, null is returned.
     */
    public static HashMap<String, MyTable> loadTables(File f) {
        if(!f.exists()) return null;
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document doc = saxBuilder.build(f);
            
            ArrayList<Element> tableEles = new ArrayList<>();
            doc.getRootElement().getDescendants(new ElementFilter("table")).forEachRemaining(tableEles::add);
            HashMap<String, MyTable>  tables = new HashMap<>();
            for(int i=0; i<tableEles.size(); i++) {
                ArrayList<Element> titleEles = new ArrayList<>();
                tableEles.get(i).getDescendants(new ElementFilter("title")).forEachRemaining(titleEles::add);
                String[] titles = new String[titleEles.size()];
                String[] defaultValues = new String[titleEles.size()];
                for(int j=0; j<titles.length; j++) {
                    Element ele = titleEles.get(j);
                    titles[j] = ele.getTextTrim();
                    defaultValues[j] = ele.getAttributeValue("defaultValue");
                }
                
                MyTable t = new MyTable(tableEles.get(i).getAttributeValue("name"), titles);
                t.nextPk = Integer.parseInt(tableEles.get(i).getAttributeValue("nextPk").trim());
                t.defaultValues = defaultValues;
                
                ArrayList<Element> rowEles = new ArrayList<>();
                tableEles.get(i).getDescendants(new ElementFilter("row")).forEachRemaining(rowEles::add);
                for(int j=0; j<rowEles.size(); j++) {
                    ArrayList<Element> cellEles = new ArrayList<>();
                    rowEles.get(j).getDescendants(new ElementFilter("cell")).forEachRemaining(cellEles::add);
                    String[] row = new String[cellEles.size()];
                    for(int k=0; k<row.length; k++) {
                        row[k] = cellEles.get(k).getText();
                    }
                    t.content.put(rowEles.get(j).getAttributeValue("pk"), row);
                }
                tables.put(t.name, t);
            }
            return tables;
        } catch(IOException | JDOMException ex) {
            Logger.getLogger(MyTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    /**
     * CAUTION: This function is susceptible to code injection!! <br>
     * Gets the parsed value for a cell found at the given column and row. <br>
     * A function begins with an '=' and can contain any operations supported in
     * the JavaScript language. Values from other cells in the same row can be
     * accessed by using the pattern 'COLUMN_TITLE' which will be replaced with
     * the actual value before parsing.
     * @param colTitle The column title of the selected cell.
     * @param rowPk The primary key of the selected row.
     * @return The parsed value.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    private String parseCell(String colTitle, String rowPk) {
        int colIndex = getColIdxByName(colTitle);
        return parseCell(colIndex, rowPk);
    }
    
    /**
     * CAUTION: This function is susceptible to code injection!! <br>
     * Gets the parsed value for a cell found at the given column and row. <br>
     * A function begins with an '=' and can contain any operations supported in
     * the JavaScript language. Values from other cells in the same row can be
     * accessed by using the pattern 'COLUMN_TITLE' which will be replaced with
     * the actual value before parsing.
     * @param colIndex The column index of the selected cell.
     * @param rowPk The primary key of the selected row.
     * @return The parsed value.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    private String parseCell(int colIndex, String rowPk) {
        String[] row = getRow(rowPk);
        return parseCell(colIndex, row);
    }
    
    /**
     * CAUTION: This function is susceptible to code injection!! <br>
     * Gets the parsed value for a cell found at the given column and row. <br>
     * A function begins with an '=' and can contain any operations supported in
     * the JavaScript language. Values from other cells in the same row can be
     * accessed by using the pattern 'COLUMN_TITLE' which will be replaced with
     * the actual value before parsing.
     * @param colIndex The column index of the selected cell.
     * @param row The row from which to parse.
     * @return The parsed value.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    private String parseCell(int colIndex, String[] row) {
        if(colIndex < 0) return null;
        String origVal = row[colIndex];
        if(origVal == null) return null;
        if(!origVal.startsWith("=")) {
            return row[colIndex];
        } else {
            origVal = origVal.substring(1);
        }
        
        for(int i=0; i<titles.length; i++) {
            origVal = origVal.replaceAll("'"+titles[i]+"'", row[i]);
        }
        
        try {
            String res = engine.eval(origVal).toString();
            return res;
        } catch (ScriptException ex) {
            Logger.getLogger(MyTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return row[colIndex];
    }
    
    /**
     * CAUTION: This function is susceptible to code injection!! <br>
     * Parses the functions found in the given row. <br>
     * A function begins with an '=' and can contain any operations supported in
     * the JavaScript language. Values from other cells in the same row can be
     * accessed by using the pattern 'COLUMN_TITLE' which will be replaced with
     * the actual value before parsing.
     * @param recordId The primary key of the row to parse.
     * @return The parsed row.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    public String[] getParsedRow(String recordId) {
        return parseRow(getRow(recordId));
    }
    
    /**
     * CAUTION: This function is susceptible to code injection!! <br>
     * Parses the functions found in the given row. <br>
     * A function begins with an '=' and can contain any operations supported in
     * the JavaScript language. Values from other cells in the same row can be
     * accessed by using the pattern 'COLUMN_TITLE' which will be replaced with
     * the actual value before parsing.
     * @param row The row to parse.
     * @return The parsed row.
     * @throws RuntimeException If the size of the row added is not equal to the
     * number of columns.
     */
    public String[] parseRow(String[] row) {
        if(row.length != titles.length)
            throw new RuntimeException("Expected row length ("+row.length+") does not match column count ("+titles.length+")");
        String[] out = new String[row.length];
        for(int i=0; i<row.length; i++) {
            out[i] = parseCell(i, row);
        }
        return out;
    }
    
    
    /**
     * Remove all records in this table. Caution: this cannot be undone.
     */
    public void clearAllRecords() {
        content.clear();
    }
    
    
    /**
     * Displays the table as a string.
     * @return The string representation of this MyTable.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTableName()).append(", ").append(getRowCount()).append("\n");
        sb.append("ID").append("\t\t");
        for(String title : titles) {
            sb.append(title).append("\t\t");
        }
        sb.append("\n");
        for(Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] row = entry.getValue();
            
            sb.append(key).append("\t\t");
            for (String cell : row) {
                sb.append(cell).append("\t\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MyTable))
            return false;
        MyTable other = (MyTable)o;
        
        if(!other.name.equals(name))
            return false;
        if(!arraysEqual(other.titles, titles))
            return false;
        if(!arraysEqual(other.defaultValues, defaultValues))
            return false;
        if(other.getColCount() != getColCount())
            return false;
        if(other.getRowCount() != getRowCount())
            return false;
        
        for (Map.Entry<String, String[]> entry : content.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if(!other.content.containsKey(key))
                return false;
            String[] otherVal = other.content.get(key);
            if(value.length != otherVal.length)
                return false;
            for(int i=0; i<value.length; i++) {
                if(!value[i].equals(otherVal[i]))
                    return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Arrays.deepHashCode(this.titles);
        hash = 47 * hash + Arrays.deepHashCode(this.defaultValues);
        return hash;
    }
    
    
    /**
     * Remove the specified element from the provided array.
     * @param arr The array from which the element is to be removed.
     * @param index The index of the element to remove.
     * @return The new array without the element removed.
     */
    public static String[] removeAt(String[] arr, int index) {
        String[] out = new String[arr.length-1];
        int mod = 0;
        for(int i=0; i<out.length; i++) {
            if(i == index)
                mod = 1;
            out[i] = arr[i+mod];
        }
        return out;
    }
    
    /**
     * Insert the specified value into the provided array.
     * @param arr
     * @param value
     * @param pos The index into which the element is inserted. If set to less
     * than 0, the position will be counted from the end of the array.
     * @return The new array with the element inserted.
     */
    public static String[] insert(String[] arr, String value, int pos) {
        if(pos < 0) pos = arr.length+1+pos;
        
        String[] out = new String[arr.length+1];
        int mod = 0;
        for(int i=0; i<out.length; i++) {
            if(i == pos) {
                mod = -1;
                continue;
            }
            out[i] = arr[i+mod];
        }
        out[pos] = value;
        return out;
    }
    
    /**
     * Compares the values of two string arrays.
     * @param arr1 The first array.
     * @param arr2 The second array.
     * @return 
     */
    public static boolean arraysEqual(String[] arr1, String[] arr2) {
        if(arr1.length != arr2.length)
            return false;
        for(int i=0; i<arr1.length; i++) {
            if(arr1[i]==null && arr2[i]==null)
                continue;
            if(arr1[i]!=null && !arr1[i].equals(arr2[i]))
                return false;
        }
        return true;
    }
    
    
    /**
     * The thread used for multiprocessing by the runOp method.
     */
    private class ProcThread implements Runnable {
        
        private boolean parse;
        private TableOperationInterface[] ops;
        private HashMap<String, String[]> rows;
        
        /**
         * Creates a new ProcThread that runs the provided operations.
         * @param parse If the rows should be parsed before being passed to the
         * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
         * method for more information on parsing.
         * @param rows The rows to pass on to the TableOperationInterface.runOp()
         * methods.
         * @param ops The operations to run on the provided rows.
         */
        public ProcThread(boolean parse, HashMap<String, String[]> rows, TableOperationInterface... ops) {
            this.parse = parse;
            this.rows = rows;
            this.ops = ops;
        }
        
        /**
         * Runs all ops on each of the provided rows.
         */
        @Override
        public void run() {
            for(Map.Entry<String, String[]> entry : rows.entrySet()) {
                String[] value = entry.getValue();
                if(parse) {
                    value = getParsedRow(entry.getKey());
                }
                for(TableOperationInterface op : ops) {
                    op.runOp(entry.getKey(), value);
                }
            }
        }
        
    }
    
    /**
     * The thread used for multiprocessing by the runOp method.
     */
    private class SeqProcThread implements Runnable {
        
        private boolean parse;
        private SequentialInterface[] ops;
        private HashMap<String, String[]> rows;
        private MyTable table;
        
        /**
         * Creates a new ProcThread that runs the provided operations.
         * @param parse If the rows should be parsed before being passed to the
         * TableOperationInterface.runOp() methods. Note: refer to the parseRow()
         * method for more information on parsing.
         * @param rows The rows to pass on to the TableOperationInterface.runOp()
         * methods.
         * @param ops The operations to run on the provided rows.
         */
        public SeqProcThread(boolean parse, HashMap<String, String[]> rows, MyTable resultTable, SequentialInterface... ops) {
            this.parse = parse;
            this.rows = rows;
            this.ops = ops;
            table = new MyTable(resultTable);
        }
        
        /**
         * Runs all ops on each of the provided rows.
         */
        @Override
        public void run() {
            for(Map.Entry<String, String[]> entry : rows.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                if(parse) {
                    value = getParsedRow(entry.getKey());
                }
                String[] newRow = value;
                for(int i=0; i<ops.length; i++) {
                    newRow = ops[i].processRow(key, newRow);
                }
                table.setRow(key, newRow);
            }
        }
        
        public MyTable getTable() {
            return table;
        }
        
    }
    
}


/*
search for item
import csv - noy needed
*/