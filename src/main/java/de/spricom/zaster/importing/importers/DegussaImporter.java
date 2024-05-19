package de.spricom.zaster.importing.importers;

import de.spricom.zaster.data.Import;
import de.spricom.zaster.importing.CsvImporter;
import de.spricom.zaster.importing.csv.CsvRow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DegussaImporter implements CsvImporter {

    @Override
    public String getName() {
        return "Degussa Bank CSV";
    }

    @Override
    public Stats process(Import imported, List<CsvRow> rows) {
        return null;
    }
}
