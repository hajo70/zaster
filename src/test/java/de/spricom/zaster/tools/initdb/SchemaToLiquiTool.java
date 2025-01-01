package de.spricom.zaster.tools.initdb;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("run manually")
public class SchemaToLiquiTool {

    @Autowired
    private DataSource dataSource;

    private final LiquiSchema schema = new LiquiSchema(this.getClass().getSimpleName());

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
        String table = "TRANSFER";
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

        exportTables(metaData);
        exportColumns(metaData);
        exportIndices(metaData);
        exportForeignKeys(metaData);

        schema.dump();
    }

    private void exportTables(DatabaseMetaData metaData) throws SQLException {
        try (ResultSet rs = metaData.getTables(null, "PUBLIC", null, null)) {
            while (rs.next()) {
                schema.add(asTable(rs));
            }
        }
    }

    private LiquiTable asTable(ResultSet rs) throws SQLException {
        LiquiTable table = new LiquiTable();
        table.setTableName(rs.getString("TABLE_NAME"));
        return table;
    }

    private void exportColumns(DatabaseMetaData metaData) throws SQLException {
        for (LiquiTable table : schema.getTables()) {
            try (ResultSet rs = metaData.getColumns(null, "PUBLIC", table.getTableName(), null)) {
                while (rs.next()) {
                    table.add(asColumn(rs));
                }
            }
        }
    }

    private LiquiColumn asColumn(ResultSet rs) throws SQLException {
        LiquiColumn column = new LiquiColumn();
        column.setColumnName(rs.getString("COLUMN_NAME"));
        column.setColumnType(switch (rs.getString("TYPE_NAME")) {
        case "NUMERIC" ->
            String.format("java.sql.Types.DECIMAL(%d, %d)", rs.getInt("COLUMN_SIZE"), rs.getInt("DECIMAL_DIGITS"));
        case "CHARACTER VARYING" -> String.format("java.sql.Types.NVARCHAR(%d)", rs.getInt("COLUMN_SIZE"));
        case "TIMESTAMP WITH TIME ZONE" -> "java.sql.Types.TIMESTAMP_WITH_TIMEZONE";
        case "BIGINT" -> "java.sql.Types.BIGINT";
        case "DATE" -> "java.sql.Types.DATE";
        case "INTEGER" -> "java.sql.Types.INTEGER";
        case "CHARACTER LARGE OBJECT" -> String.format("java.sql.Types.CLOB(%d)", rs.getInt("COLUMN_SIZE"));
        default -> throw new IllegalArgumentException(
                "Not supported: " + rs.getString("TYPE_NAME") + " for " + column.getColumnName());
        });
        column.setPrimaryKey("ID".equals(column.getColumnName()));
        column.setNullable(rs.getBoolean("IS_NULLABLE"));
        return column;
    }

    private void exportIndices(DatabaseMetaData metaData) throws SQLException {
        for (LiquiTable table : schema.getTables()) {
            try (ResultSet rs = metaData.getIndexInfo(null, "PUBLIC", table.getTableName(), false, false)) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    LiquiColumn column = table.getColumn(rs.getString("COLUMN_NAME"));
                    int pos = rs.getInt("ORDINAL_POSITION");
                    LiquiIndex index = table.getIndices().computeIfAbsent(indexName, LiquiIndex::new);
                    index.add(pos, column);
                    index.setUnique(!rs.getBoolean("NON_UNIQUE"));
                }
                for (LiquiIndex index : table.getIndices().values()) {
                    if (index.isUnique() && index.getColumns().size() == 1) {
                        index.getColumns().get(0).setUnique(true);
                    }
                }
            }
        }
    }

    private void exportForeignKeys(DatabaseMetaData metaData) throws SQLException {
        for (LiquiTable table : schema.getTables()) {
            try (ResultSet rs = metaData.getImportedKeys(null, "PUBLIC", table.getTableName())) {
                while (rs.next()) {
                    assertThat(rs.getString("FKTABLE_NAME")).isEqualTo(table.getTableName());
                    LiquiColumn column = table.getColumn(rs.getString("FKCOLUMN_NAME"));
                    LiquiForeignKey foreignKey = new LiquiForeignKey();
                    column.setForeignKey(foreignKey);
                    foreignKey.setForeignKeyName(rs.getString("FK_NAME"));
                    foreignKey.setReferences(schema.getTable(rs.getString("PKTABLE_NAME")));
                    foreignKey.setOnDeleteCascade(rs.getBoolean("DELETE_RULE"));
                }
            }
        }
    }
}
