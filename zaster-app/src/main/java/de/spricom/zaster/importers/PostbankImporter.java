package de.spricom.zaster.importers;

import de.spricom.zaster.repository.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class PostbankImporter implements CsvImporter {

    private final BookingService bookingService;


    @Override
    public String getName() {
        return "Postbank CSV";
    }

    @Override
    public void process(List<CsvRow> rows) {

    }
}
