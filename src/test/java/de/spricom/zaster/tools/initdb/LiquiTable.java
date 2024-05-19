package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class LiquiTable {

    private LiquiSchema schema;
    private String tableName;
    private final List<LiquiColumn> columns = new ArrayList<>();
    private final List<LiquiTable> collectionTables = new ArrayList<>();
    private final Map<String, LiquiIndex> indices = new TreeMap<>();

    private Class<?> entity;

    public void add(LiquiColumn column) {
        column.setTable(this);
        columns.add(column);
    }

    public LiquiColumn getColumn(String columnName) {
        return columns.stream()
                .filter(c -> columnName.equals(c.getColumnName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("There is no " + columnName
                        + " column in table " + getTableName() + "."));
    }

    void export(PrintStream out) {
        out.println("        - createTable:");
        out.println("            tableName: " + tableName);
        out.println("            columns:");
        for (LiquiColumn column : columns) {
            column.export(out);
        }
        for (LiquiTable collectionTable : collectionTables) {
            collectionTable.export(out);
        }
    }
}
