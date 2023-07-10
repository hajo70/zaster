package de.spricom.zaster.importers;

import org.springframework.stereotype.Component;

@Component
public class PostbankImporter implements CsvImporter {

    @Override
    public String getName() {
        return "Postbank CSV";
    }
}
