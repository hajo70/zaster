package de.spricom.zaster.importing.importers;

import de.spricom.zaster.entities.managment.TenantEntity;
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
    public void process(TenantEntity tenant, List<CsvRow> rows) {

    }
}
