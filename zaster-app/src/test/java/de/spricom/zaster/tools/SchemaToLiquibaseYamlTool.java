package de.spricom.zaster.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
public class SchemaToLiquibaseYamlTool {

    @Autowired
    private DataSource dataSource;

    private List<Table> tables = new ArrayList<>();

    @Test
    void dumpSchema() throws SQLException {
        Connection con = dataSource.getConnection();
        DatabaseMetaData metaData = con.getMetaData();
        System.out.println("=== Catalogs:");
        dump(metaData.getCatalogs());
        System.out.println("=== Schemas:");
        dump(metaData.getSchemas());
        System.out.println("=== Tables:");
        dump(metaData.getTables(null, "PUBLIC%", null, null));
        String table = "APPLICATION_USER";
        System.out.printf("=== %s Columns:%n", table);
        dump(metaData.getColumns(null, "PUBLIC%", table, null));
        System.out.printf("=== %s Indexes:%n", table);
        dump(metaData.getIndexInfo(null, "PUBLIC", table, false, false));
        System.out.printf("=== %s Imported Keys:%n", table);
        dump(metaData.getImportedKeys(null, "PUBLIC", table));
        System.out.printf("=== %s Exported Keys:%n", table);
        dump(metaData.getExportedKeys(null, "PUBLIC", table));
    }

    private void dump(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            System.out.print(metaData.getColumnName(i));
            if (i < count) {
                System.out.print("; ");
            }
        }
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= count; i++) {
                System.out.print(rs.getString(i));
                if (i < count) {
                    System.out.print("; ");
                }
            }
            System.out.println();
        }
    }

    @Test
    void exportSchema() throws SQLException {
        Connection con = dataSource.getConnection();
        DatabaseMetaData metaData = con.getMetaData();

        try (ResultSet rs = metaData.getTables(null, "PUBLIC", null, null)) {
            while (rs.next()) {
                tables.add(new Table(rs));
            }
        }

        for (Table table : tables) {
            try (ResultSet rs = metaData.getColumns(null, "PUBLIC", table.name, null)) {
                while (rs.next()) {
                    table.columns.add(new Column(table, rs));
                }
            }
        }

        for (Table table : tables) {
            try (ResultSet rs = metaData.getIndexInfo(null, "PUBLIC", table.name, false, false)) {
                while (rs.next()) {
                    Index index = new Index(table, rs);
                    table.indices.computeIfAbsent(index.name, name -> new ArrayList()).add(index);
                }
                for (List<Index> indices : table.indices.values()) {
                    if (indices.size() == 1) {
                        Index index = indices.get(0);
                        index.column.unique = !index.nonUnique;
                    }
                }
            }
        }

        for (Table table : tables) {
            try (ResultSet rs = metaData.getImportedKeys(null, "PUBLIC", table.name)) {
                while (rs.next()) {
                    ForeignKey foreignKey = new ForeignKey(tables, rs);
                    foreignKey.column.foreignKey = foreignKey;
                    foreignKey.references.referenceCount--;
                }
            }
        }

        exportYaml(System.out);
    }

    private void exportYaml(PrintStream out) {
        out.println("databaseChangeLog:");
        out.println("  - changeSet:");
        out.println("      id: 1");
        out.println("      author: SchemaToLiquibaseYamlTool");
        out.println("      changes:");
        tables.sort(Comparator.comparing(table -> table.referenceCount));
        for (Table table : tables) {
            table.export(out);
        }
    }

    static class ForeignKey {
        final Column column;
        final Table references;
        final boolean onDeleteCascade;

        ForeignKey(List<Table> tables, ResultSet rs) throws SQLException {
            references = lookupTable(tables, rs.getString("PKTABLE_NAME"));
            onDeleteCascade = rs.getBoolean("DELETE_RULE");
            column = lookupTable(tables, rs.getString("FKTABLE_NAME")).lookupColumn(rs.getString("FKCOLUMN_NAME"));
        }

        static Table lookupTable(List<Table> tables, String tableName) {
            for (Table table : tables) {
                if (tableName.equals(table.name)) {
                    return table;
                }
            }
            throw new IllegalArgumentException("There is no " + tableName + " table.");
        }

        void export(PrintStream out) {
            out.println("                    foreignKeyName: FK_" + column.table.name + "_" + column.name + "_" + references.name);
            out.println("                    referencedTableName: " + references.name);
            out.println("                    referencedColumnNames: ID");
            if (onDeleteCascade) {
                out.println("                    deleteCascade: true");
            }
        }
    }

    static class Column {
        final Table table;
        final String name;
        final String type;
        final boolean nullable;

        final boolean primaryKey;
        boolean unique = false;
        ForeignKey foreignKey;

        Column(Table table, ResultSet rs) throws SQLException {
            this.table = table;
            name = rs.getString("COLUMN_NAME");
            type = switch (rs.getString("TYPE_NAME")) {
                case "NUMERIC" ->
                        String.format("java.sql.Types.DECIMAL(%d, %d)", rs.getInt("COLUMN_SIZE"), rs.getInt("DECIMAL_DIGITS"));
                case "CHARACTER VARYING" -> String.format("java.sql.Types.NVARCHAR(%d)", rs.getInt("COLUMN_SIZE"));
                case "TIMESTAMP WITH TIME ZONE" -> "java.sql.Types.TIMESTAMP_WITH_TIMEZONE";
                case "BIGINT" -> "java.sql.Types.BIGINT";
                case "DATE" -> "java.sql.Types.DATE";
                case "CHARACTER LARGE OBJECT" -> String.format("java.sql.Types.CLOB(%d)", rs.getInt("COLUMN_SIZE"));
                default ->
                        throw new IllegalArgumentException("Not supported: " + rs.getString("TYPE_NAME") + " for " + name + " in " + table.name);
            };
            primaryKey = "ID".equals(name);
            nullable = rs.getBoolean("IS_NULLABLE");
        }

        void export(PrintStream out) {
            out.println("              - column:");
            out.println("                  name: " + name);
            out.println("                  type: " + type);
            if (primaryKey || unique || !nullable || foreignKey != null) {
                out.println("                  constraints:");
                if (primaryKey) {
                    out.println("                    primaryKey: true");
                    out.println("                    primaryKeyNAME: PK_" + table.name);
                } else {
                    if (unique) {
                        out.println("                    unique: true");
                    }
                    if (!nullable) {
                        out.println("                    nullable: false");
                    }
                }
                if (foreignKey != null) {
                    foreignKey.export(out);
                }
            }
        }
    }

    static class Index {
        final String name;
        final Column column;
        final boolean nonUnique;
        final int pos;

        Index(Table table, ResultSet rs) throws SQLException {
            name = rs.getString("INDEX_NAME");
            column = table.lookupColumn(rs.getString("COLUMN_NAME"));
            nonUnique = rs.getBoolean("NON_UNIQUE");
            pos = rs.getInt("ORDINAL_POSITION");
        }
    }

    static class Table {
        final String name;
        final List<Column> columns = new ArrayList<>();
        final Map<String, List<Index>> indices = new HashMap<>();
        int referenceCount;

        Table(ResultSet rs) throws SQLException {
            name = rs.getString("TABLE_NAME");
        }

        void export(PrintStream out) {
            out.println("        - createTable:");
            out.println("            tableName: " + name);
            out.println("            columns:");
            for (Column column : columns) {
                column.export(out);
            }
        }

        Column lookupColumn(String columnName) {
            for (Column column : columns) {
                if (columnName.equals(column.name)) {
                    return column;
                }
            }
            throw new IllegalArgumentException("There is no " + columnName + " column in " + name + " table.");
        }
    }
}
