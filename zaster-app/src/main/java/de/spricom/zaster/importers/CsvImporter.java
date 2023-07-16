package de.spricom.zaster.importers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface CsvImporter {

    String getName();

    void process(List<CsvRow> rows);

    default Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    default String getDelimiter() {
        return ";";
    }
}
