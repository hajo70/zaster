package de.spricom.zaster.importers;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DegussaImporter implements CsvImporter {

    @Override
    public String getName() {
        return "Degussa Bank CSV";
    }

    @Override
    public void process(List<CsvRow> rows) {

    }
}
