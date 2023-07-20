package de.spricom.zaster.importing;

import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.importing.csv.CsvRow;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface CsvImporter {

    String getName();

    void process(TenantEntity tenant, List<CsvRow> rows);

    default Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    default String getDelimiter() {
        return ";";
    }
}
