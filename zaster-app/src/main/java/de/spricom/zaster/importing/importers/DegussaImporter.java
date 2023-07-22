package de.spricom.zaster.importing.importers;

import de.spricom.zaster.entities.tracking.ImportEntity;
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
    public Stats process(ImportEntity imported, List<CsvRow> rows) {
        return null;
    }
}
