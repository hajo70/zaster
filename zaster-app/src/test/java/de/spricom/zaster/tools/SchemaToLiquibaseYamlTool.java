package de.spricom.zaster.tools;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.*;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.dessertj.partitioning.ClazzPredicates;
import org.dessertj.slicing.Classpath;
import org.dessertj.slicing.Slice;
import org.dessertj.util.AnnotationPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @SpringBootTest
// @ActiveProfiles("test")
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
                }
            }
        }

        exportYaml(System.out);
    }

    @Test
    void listEntities() {
        Classpath cp = new Classpath();
        String packageName = TenantEntity.class.getPackageName();
        String entitiesPackage = packageName.substring(0, packageName.indexOf("entities")) + "entities";
        Slice entities = cp.packageTreeOf(entitiesPackage)
                .slice(ClazzPredicates.matchesAnnotation(AnnotationPattern.of(Entity.class)));
        entities.getClazzes().forEach(clazz ->
                System.out.printf("tables.add(new Table(%s.class));%n", clazz.getShortName()));
    }

    @Test
    void exportEntities() {
        tables.add(new Table(TenantEntity.class));
        tables.add(new Table(ApplicationUserEntity.class));
        tables.add(new Table(ImportEntity.class));
        tables.add(new Table(FileSourceEntity.class));
        tables.add(new Table(CurrencyEntity.class));
        tables.add(new Table(AccountGroupEntity.class));
        tables.add(new Table(AccountEntity.class));
        tables.add(new Table(SnapshotEntity.class));
        tables.add(new Table(TransactionEntity.class));
        tables.add(new Table(BookingEntity.class));

        exportYaml(System.out);
    }

    private void exportYaml(PrintStream out) {
        out.println("databaseChangeLog:");
        out.println("  - changeSet:");
        out.println("      id: 1");
        out.println("      author: SchemaToLiquibaseYamlTool");
        out.println("      changes:");
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

        Column(Table table, Field field) {
            this.table = table;
            jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);
            if (AbstractEntity.class.isAssignableFrom(field.getType())) {
                this.primaryKey = false;
                this.name = camelTo_(field.getName()) + "_ID";
                this.type = String.format("java.sql.Types.NVARCHAR(%d)", 63);
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                this.nullable = joinColumn == null || joinColumn.nullable();
            } else if (field.getType().isEnum()) {
                this.primaryKey = false;
                this.name = columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.name())
                        ? columnAnnotation.name()
                        : camelTo_(field.getName());
                this.type = switch (field.getAnnotation(Enumerated.class).value()) {
                    case STRING -> String.format("java.sql.Types.NVARCHAR(%d)",
                            field.isAnnotationPresent(jakarta.persistence.Column.class)
                                    ? field.getAnnotation(jakarta.persistence.Column.class).length()
                                    : 63);
                    case ORDINAL -> "int";
                };
                this.nullable = isNullable(field);
            } else {
                this.name = columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.name())
                        ? columnAnnotation.name()
                        : camelTo_(field.getName());
                this.type = switch (field.getType().getSimpleName()) {
                    case "String" -> String.format("java.sql.Types.NVARCHAR(%d)", fieldLength(field));
                    case "Long" -> "java.sql.Types.BIGINT";
                    case "BigDecimal" -> "java.sql.Types.DECIMAL(40, 15)";
                    case "Instant" -> "java.sql.Types.TIMESTAMP_WITH_TIMEZONE";
                    case "Locale" -> "java.sql.Types.NVARCHAR(63)";
                    case "ZoneId" -> "java.sql.Types.NVARCHAR(63)";
                    default -> throw new IllegalArgumentException("Not supported: "
                            + field.getType() + " for " + name + " in " + table.name);
                };
                this.primaryKey = "ID".equals(this.name);
                this.nullable = isNullable(field);
            }
        }

        private static int fieldLength(Field field) {
            if (field.isAnnotationPresent(Id.class)) {
                return 63;
            } else if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
                return field.getAnnotation(jakarta.persistence.Column.class).length();
            }
            return 255;
        }

        private static String camelTo_(String name) {
            StringBuilder sb = new StringBuilder(name.length() + 5);
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (Character.isUpperCase(ch)
                        && i > 0
                        && !Character.isUpperCase(name.charAt(i - 1))) {
                    sb.append("_");
                }
                sb.append(Character.toUpperCase(ch));
            }
            return sb.toString();
        }

        private static boolean isNullable(Field field) {
            return field.isAnnotationPresent(jakarta.persistence.Column.class)
                    ? field.getAnnotation(jakarta.persistence.Column.class).nullable()
                    : true;
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

        Table(ResultSet rs) throws SQLException {
            name = rs.getString("TABLE_NAME");
        }

        Table(Class<?> entityClass) {
            jakarta.persistence.Table tableAnnotation = entityClass.getAnnotation(jakarta.persistence.Table.class);
            name = tableAnnotation.name();
            for (Field field : entityClass.getSuperclass().getDeclaredFields()) {
                if (isColumn(field)) {
                    columns.add(new Column(this, field));
                }
            }
            for (Field field : entityClass.getDeclaredFields()) {
                if (isColumn(field)) {
                    columns.add(new Column(this, field));
                }
            }
        }

        private static boolean isColumn(Field field) {
            if (field.isAnnotationPresent(Transient.class)
                    || field.isAnnotationPresent(Embedded.class)
                    || field.isAnnotationPresent(Enumerated.class) && field.isAnnotationPresent(ElementCollection.class)
                    || field.isAnnotationPresent(OneToMany.class)) {
                return false;
            }
            return true;
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
