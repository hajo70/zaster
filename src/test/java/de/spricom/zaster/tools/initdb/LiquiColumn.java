package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.io.PrintStream;
import java.lang.reflect.Field;

@Data
public class LiquiColumn {
    public static final String ID_TYPE = "java.sql.Types.NVARCHAR(63)";

    private LiquiTable table;
    private String columnName;
    private String columnType;
    private boolean primaryKey;
    private boolean nullable = true;
    private boolean unique;
    private LiquiForeignKey foreignKey;

    private Field field;

    void export(PrintStream out) {
        out.println("              - column:");
        out.println("                  name: " + columnName);
        out.println("                  type: " + columnType);
        if (primaryKey || unique || !nullable || foreignKey != null) {
            out.println("                  constraints:");
            if (primaryKey) {
                out.println("                    primaryKey: true");
                out.println("                    primaryKeyName: PK_" + table.getTableName());
            } else {
                if (unique) {
                    out.println("                    unique: true");
                    out.println(
                            "                    uniqueConstraintName: UQ_" + table.getTableName() + "_" + columnName);
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
