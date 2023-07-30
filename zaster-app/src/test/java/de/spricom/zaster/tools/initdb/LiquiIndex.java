package de.spricom.zaster.tools.initdb;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LiquiIndex {
    private final String indexName;
    private final List<LiquiColumn> columns = new ArrayList<>();
    private boolean unique;

    void add(int pos, LiquiColumn column) {
        while (columns.size() <= pos) {
            columns.add(null);
        }
        columns.set(pos, column);
    }
}
