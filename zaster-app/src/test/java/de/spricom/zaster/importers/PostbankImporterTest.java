package de.spricom.zaster.importers;

import de.spricom.zaster.repository.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@MockitoSettings
class PostbankImporterTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private PostbankImporter importer;

    @Test
    void testProcess() throws IOException {
        var csv = scan(new File("/Users/hajo/Desktop/Konto/Postbank/Kontoumsaetze_210_5140645_00_20230709_134540.csv"));
        importer.process(csv);
    }

    private List<CsvRow> scan(File file) throws IOException {
        CsvReader csvReader = new CsvReader(";", StandardCharsets.UTF_8);
        try (InputStream is = new FileInputStream(file)) {
            return csvReader.scan(is);
        }
    }
}