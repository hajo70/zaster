package de.spricom.zaster.importers;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

class CsvReaderTest {

    @Test
    void testScanLocalFile() throws IOException {
        var csv = scan(new File("/Users/hajo/Desktop/Konto/Postbank/Kontoumsaetze_210_5140645_00_20230709_134540.csv"));
        for (CsvRow row : csv.subList(8, csv.size() - 1)) {
            System.out.printf("%10s %12s %-50s %-40s%n",
                    row.column("A"),
                    row.column("L"),
                    row.column("D"),
                    row.column("E")
                    );
        }
    }

    private List<CsvRow> scan(File file) throws IOException {
        var bytes = Files.readAllBytes(file.toPath());
        System.out.println("MD5: " + DigestUtils.md5Hex(bytes));
        CsvReader csvReader = new CsvReader();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            return csvReader.scan(is);
        }
    }
}