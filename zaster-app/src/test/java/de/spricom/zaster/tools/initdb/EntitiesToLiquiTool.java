package de.spricom.zaster.tools.initdb;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.managment.UserEntity;
import de.spricom.zaster.entities.tracking.*;
import jakarta.persistence.*;
import org.dessertj.partitioning.ClazzPredicates;
import org.dessertj.slicing.Classpath;
import org.dessertj.slicing.Slice;
import org.dessertj.util.AnnotationPattern;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntitiesToLiquiTool {

    private final LiquiSchema schema = new LiquiSchema(this.getClass().getSimpleName());

    @Test
    void listEntities() {
        Classpath cp = new Classpath();
        String packageName = TenantEntity.class.getPackageName();
        String entitiesPackage = packageName.substring(0, packageName.indexOf("entities")) + "entities";
        Slice entities = cp.packageTreeOf(entitiesPackage)
                .slice(ClazzPredicates.matchesAnnotation(AnnotationPattern.of(Entity.class)));
        entities.getClazzes().forEach(clazz ->
                System.out.printf("schema.add(asLiquiTable(%s.class));%n", clazz.getShortName()));
    }

    @Test
    void exportEntities() throws FileNotFoundException {
        schema.add(asLiquiTable(TenantEntity.class));
        schema.add(asLiquiTable(UserEntity.class));
        schema.add(asLiquiTable(ImportEntity.class));
        schema.add(asLiquiTable(FileSourceEntity.class));
        schema.add(asLiquiTable(CurrencyEntity.class));
        schema.add(asLiquiTable(AccountEntity.class));
        schema.add(asLiquiTable(AccountCurrencyEntity.class));
        schema.add(asLiquiTable(SnapshotEntity.class));
        schema.add(asLiquiTable(BookingEntity.class));
        schema.add(asLiquiTable(TransferEntity.class));
        updateForeignKeys();

        schema.dump();
        schema.exportYaml(LiquiSchema.CHANGELOG_100);
    }

    private void updateForeignKeys() {
        Map<Class<?>, LiquiTable> tables =
                schema.getTables().stream()
                        .collect(Collectors.toMap(LiquiTable::getEntity, Function.identity()));
        for (LiquiTable table : schema.getTables()) {
            for (LiquiColumn column : table.getColumns()) {
                if (column.getForeignKey() != null && column.getForeignKey().getReferences() == null) {
                    column.getForeignKey().setReferences(tables.get(column.getField().getType()));
                }
            }
        }
    }

    private LiquiTable asLiquiTable(Class<? extends AbstractEntity> entity) {
        var table = new LiquiTable();
        table.setEntity(entity);
        table.setTableName(tableName(entity));
        addColumns(table, entity.getSuperclass());
        addColumns(table, entity);
        return table;
    }

    private String tableName(Class<? extends AbstractEntity> entity) {
        Table tab = entity.getAnnotation(Table.class);
        return tab != null ? tab.name() : camelTo_(entity.getSimpleName());
    }

    private void addColumns(LiquiTable table, Class<?> entity) {
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Embedded.class)) {
                addEmbeddedColumns(table, field);
            } else if (field.isAnnotationPresent(Enumerated.class)
                    && field.isAnnotationPresent(ElementCollection.class)) {
                addCollectionTable(table, field);
            } else if (!field.isAnnotationPresent(Transient.class)
                    && !field.isAnnotationPresent(OneToMany.class)) {
                table.add(asLiquiColumn(field));
            }
        }
    }

    private void addEmbeddedColumns(LiquiTable table, Field field) {
        LiquiTable embeddedTable = new LiquiTable();
        addColumns(embeddedTable, field.getType());
        Map<String, Column> overrides =
                Arrays.stream(field.getAnnotationsByType(AttributeOverride.class))
                        .collect(Collectors.toMap(AttributeOverride::name, AttributeOverride::column));
        for (LiquiColumn column : embeddedTable.getColumns()) {
            Column override = overrides.get(column.getField().getName());
            if (override != null && !override.name().isEmpty()) {
                column.setColumnName(override.name());
            } else {
                column.setColumnName(camelTo_(field.getName()) + "_" + column.getColumnName());
            }
            table.add(column);
        }
    }

    private void addCollectionTable(LiquiTable table, Field field) {
        CollectionTable col = field.getAnnotation(CollectionTable.class);
        var collectionTable = new LiquiTable();
        collectionTable.setTableName(col != null && !col.name().isEmpty()
                ? col.name()
                : table.getTableName() + "_" + field.getName().toUpperCase());
        for (JoinColumn joinColumn : col.joinColumns()) {
            var column = new LiquiColumn();
            column.setColumnName(joinColumn.name());
            column.setColumnType(LiquiColumn.ID_TYPE);
            column.setNullable(false);
            collectionTable.add(column);
            var foreignKey = new LiquiForeignKey();
            foreignKey.setColumn(column);
            foreignKey.setReferences(table);
            foreignKey.setOnDeleteCascade(true);
            foreignKey.setForeignKeyName("FK_" + collectionTable.getTableName());
            column.setForeignKey(foreignKey);
        }
        var column = new LiquiColumn();
        column.setColumnName(columName(field));
        column.setColumnType(enumType(field));
        column.setNullable(false);
        collectionTable.add(column);
        table.getCollectionTables().add(collectionTable);
    }

    private LiquiColumn asLiquiColumn(Field field) {
        var column = new LiquiColumn();
        column.setField(field);
        if (isId(field)) {
            handleId(column);
        } else if (isEntityRelation(field)) {
            handleEntityRelation(column);
        } else {
            column.setColumnName(columName(field));
            column.setColumnType(columnType(field));
            column.setNullable(isNullable(field));
        }
        column.setUnique(isUnique(field));
        return column;
    }

    private boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private void handleId(LiquiColumn column) {
        column.setColumnName("ID");
        column.setColumnType(LiquiColumn.ID_TYPE);
        column.setPrimaryKey(true);
    }

    private boolean isEntityRelation(Field field) {
        return AbstractEntity.class.isAssignableFrom(field.getType());
    }

    private void handleEntityRelation(LiquiColumn column) {
        column.setColumnName(camelTo_(column.getField().getName()) + "_ID");
        column.setColumnType(LiquiColumn.ID_TYPE);
        JoinColumn joinColumn = column.getField().getAnnotation(JoinColumn.class);
        column.setNullable(joinColumn == null || joinColumn.nullable());
        var foreignKey = new LiquiForeignKey();
        foreignKey.setColumn(column);
        if (column.getField().isAnnotationPresent(OneToMany.class)) {
            foreignKey.setOnDeleteCascade(column.getField().getAnnotation(OneToMany.class).orphanRemoval());
        } else if (column.getField().isAnnotationPresent(OneToOne.class)) {
            foreignKey.setOnDeleteCascade(column.getField().getAnnotation(OneToOne.class).orphanRemoval());
        }
        column.setForeignKey(foreignKey);
    }

    private String columName(Field field) {
        Column col = field.getAnnotation(Column.class);
        if (col != null && !col.name().isEmpty()) {
            return col.name();
        }
        return camelTo_(field.getName());
    }

    private String camelTo_(String name) {
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

    private String columnType(Field field) {
        if (field.getType().isEnum()) {
            return enumType(field);
        }
        return switch (field.getType().getSimpleName()) {
            case "String" -> stringType(field);
            case "Long" -> "java.sql.Types.BIGINT";
            case "Integer" -> "java.sql.Types.INTEGER";
            case "BigDecimal" -> "java.sql.Types.DECIMAL(40, 15)";
            case "LocalDate" -> "java.sql.Types.DATE";
            case "LocalTime" -> "java.sql.Types.TIME";
            case "Instant" -> "java.sql.Types.TIMESTAMP_WITH_TIMEZONE";
            case "ZoneOffset" -> "java.sql.Types.NVARCHAR(6)";
            case "Locale", "ZoneId" -> "java.sql.Types.NVARCHAR(63)";
            default -> throw new IllegalArgumentException("Not supported: " + field.getType());
        };
    }

    private String stringType(Field field) {
        return field.isAnnotationPresent(Lob.class)
                ? String.format("java.sql.Types.LONGNVARCHAR(%d)", fieldLength(field, 65535))
                : String.format("java.sql.Types.NVARCHAR(%d)", fieldLength(field, 255));
    }

    private String enumType(Field field) {
        return switch (field.getAnnotation(Enumerated.class).value()) {
            case STRING -> String.format("java.sql.Types.NVARCHAR(%d)", fieldLength(field, 63));
            case ORDINAL -> "int";
        };
    }

    private int fieldLength(Field field, int defaultLength) {
        if (field.isAnnotationPresent(Id.class)) {
            return 63;
        } else if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).length();
        }
        return defaultLength;
    }

    private boolean isNullable(Field field) {
        return !field.isAnnotationPresent(Column.class)
                || field.getAnnotation(Column.class).nullable();
    }

    private boolean isUnique(Field field) {
        return field.isAnnotationPresent(Column.class)
                && field.getAnnotation(Column.class).unique();
    }
}
