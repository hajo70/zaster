package de.spricom.zaster.importers;

import de.spricom.zaster.repository.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class PostbankImporter implements CsvImporter {
    private static final String[] HEADER_COLUMNS = {
            "Buchungstag", // A
            "Wert", // B
            "Umsatzart", // C
            "Begünstigter / Auftraggeber", // D
            "Verwendungszweck", // E
            "IBAN / Kontonummer", // F
            "BIC", // G
            "Kundenreferenz", // H
            "Mandatsreferenz", // I
            "Gläubiger ID", // J
            "Fremde Gebühren", // K
            "Betrag", // L
            "Abweichender Empfänger", // M
            "Anzahl der Aufträge", // N
            "Anzahl der Schecks", // O
            "Soll", // P
            "Haben", // Q
            "Währung" // R
    };

    private final BookingService bookingService;

    @Override
    public String getName() {
        return "Postbank CSV";
    }

    @Override
    public void process(List<CsvRow> rows) {


        var header = rows.get(7);
        checkHeader(header);

        for (CsvRow row : rows.subList(8, rows.size() - 1)) {
            var booking = toRecord(row);
            System.out.println(booking);
        }
    }

    private BookingService.BookingRecord toRecord(CsvRow row) {
        return new BookingService.BookingRecord(
                parseDate(row.column("A")),
                parseDate(row.column("B")),
                row.column("F"),
                row.column("D"),
                concat(row.column("C"), row.column("E"), ": "),
                parseMoney(row.column("L")),
                details(row),
                row.md5()
        );
    }

    private LocalDate parseDate(String date) {
        return LocalDate.from(DateTimeFormatter.ofPattern("d.M.yyyy").parse(date));
    }

    private BigDecimal parseMoney(String money) {
        return new BigDecimal(money.replace(".", "").replace(",", "."));
    }

    private String concat(String a, String b, String sep) {
        if (a.isBlank()) {
            return b;
        }
        if (b.isBlank()) {
            return a;
        }
        return a + sep + b;
    }

    private Map<String, String> details(CsvRow row) {
        Map<String, String> details = new HashMap<>(HEADER_COLUMNS.length);
        for (int i = 0; i < HEADER_COLUMNS.length; i++) {
            if (!row.column(i).isBlank()) {
                details.put(HEADER_COLUMNS[i], row.column(i));
            }
        }
        return Collections.unmodifiableMap(details);
    }

    private void checkHeader(CsvRow header) {
        int index = 0;
        for (String column : header.columns()) {
            if (!HEADER_COLUMNS[index].equals(column)) {
                throw new IllegalArgumentException("Unexpected header column " +
                        (char) ('A' + index) + ": " + column +
                        ", expected: " + HEADER_COLUMNS[index]);
            }
            index++;
        }
    }

    private void dumpHeader(CsvRow header) {
        int index = 0;
        for (String column : header.columns()) {
            System.out.println("\"" + column + "\", // " + (char) ('A' + index));
            index++;
        }
    }
}
