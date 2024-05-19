package de.spricom.zaster.importing;

import de.spricom.zaster.data.Import;
import de.spricom.zaster.importing.csv.CsvRow;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface CsvImporter {

    String getName();

    Stats process(Import imported, List<CsvRow> rows);

    default Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    default String getDelimiter() {
        return ";";
    }

    record Stats(
            int totalCount,
            int importedCount
    ) {
    }
}
