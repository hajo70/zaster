package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@Data
public class LiquiTable {

    private LiquiSchema schema;
    private String tableName;
    private final List<LiquiColumn> columns = new ArrayList<>();
    private final List<LiquiTable> collectionTables = new ArrayList<>();

    private Class<?> entity;

    public void add(LiquiColumn column) {
        column.setTable(this);
        columns.add(column);
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
