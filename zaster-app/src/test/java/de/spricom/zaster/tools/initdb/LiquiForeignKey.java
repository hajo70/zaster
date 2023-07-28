package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.io.PrintStream;

@Data
public class LiquiForeignKey {
    private LiquiColumn column;
    private LiquiTable references;
    private boolean onDeleteCascade;

    void export(PrintStream out) {
        out.println("                    foreignKeyName: "
                + "FK_" + column.getTable().getTableName()
                + "_" + column.getColumnName()
                + "_" + references.getTableName());
        out.println("                    referencedTableName: " + references.getTableName());
        out.println("                    referencedColumnNames: ID");
        if (onDeleteCascade) {
            out.println("                    deleteCascade: true");
        }
    }

}
