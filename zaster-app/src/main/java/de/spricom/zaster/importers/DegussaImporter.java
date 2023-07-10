package de.spricom.zaster.importers;

import org.springframework.stereotype.Component;

@Component
public class DegussaImporter implements CsvImporter {

    @Override
    public String getName() {
        return "Degussa Bank CSV";
    }
}
