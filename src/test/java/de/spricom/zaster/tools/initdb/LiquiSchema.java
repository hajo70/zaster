package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@Data
public final class LiquiSchema {
    public static final File CHANGELOG_100 = new File("src/main/resources/db/changelog/changelog-1.0.0.yaml");

    private final List<LiquiTable> tables = new ArrayList<>();
    private final String author;

    public void add(LiquiTable table) {
        table.setSchema(this);
        tables.add(table);
    }

    public LiquiTable getTable(String tableName) {
        return tables.stream()
                .filter(t -> tableName.equals(t.getTableName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("There is no " + tableName + " table."));
    }

    public void exportYaml(PrintStream out) {
        out.println("databaseChangeLog:");
        out.println("  - changeSet:");
        out.println("      id: 1");
        out.println("      author: " + author);
        out.println("      changes:");
        for (LiquiTable table : tables) {
            table.export(out);
        }
    }

    public void exportYaml(File file) throws FileNotFoundException {
        try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
            exportYaml(ps);
        }
    }

    public void dump() {
        exportYaml(System.out);
    }
}
