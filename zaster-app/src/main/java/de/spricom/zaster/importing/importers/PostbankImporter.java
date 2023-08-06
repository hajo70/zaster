package de.spricom.zaster.importing.importers;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.importing.CsvImporter;
import de.spricom.zaster.importing.csv.CsvRow;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
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

    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;

    @Override
    public String getName() {
        return "Postbank CSV";
    }

    @Override
    public Stats process(ImportEntity imported, List<CsvRow> rows) {
        checkHeader(rows.get(1), new String[] {
                "Konto", // A
                "Filial-/Kontonummer", // B
                "IBAN", // C
                "Währung", // D
        });

        AccountCurrencyEntity account = getAccount(imported.getTenant(), rows.get(2));

        var header = rows.get(7);
        checkHeader(header, HEADER_COLUMNS);

        int totalCount = rows.size() - 9;
        int importedCount = 0;
        for (CsvRow row : rows.subList(8, rows.size() - 1)) {
            var booking = toRecord(row);
            if (bookingService.addTransaction(imported, account, booking)) {
                importedCount++;
            }
        }
        if (importedCount > 0) {
            bookingService.addSnapshot(imported, account, toSnapshot(rows.get(rows.size() - 1)));
        }
        return new Stats(totalCount, importedCount);
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

    private TrackingDateTime parseDate(String date) {
        return TrackingDateTime.of(LocalDate.from(DateTimeFormatter.ofPattern("d.M.yyyy").parse(date)));
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

    private BookingService.SnapshotRecord toSnapshot(CsvRow snapshotRow) {
        check(snapshotRow, "A", "Kontostand");
        return new BookingService.SnapshotRecord(
        parseDate(snapshotRow.column("B")),
        parseMoney(snapshotRow.column("E")));
    }

    private AccountCurrencyEntity getAccount(TenantEntity tenant, CsvRow accountRow) {
        String accountName = accountRow.column("A");
        String iban = accountRow.column("C");
        String currencyCode = accountRow.column("D");
        CurrencyEntity currency = currencyService.getOrCreateCurrencyByCode(tenant, currencyCode);
        return accountService.getOrCreateAccount(tenant, iban, accountName, currency);
    }

    private void checkHeader(CsvRow header, String[] columns) {
        for (int i = 0; i < header.columns().length; i++) {
            check(header, CsvRow.index(i), columns[i]);
        }
    }

    private void check(CsvRow row, String index, String expectedHeader) {
        String actualHeader = row.column(index);
        if (!expectedHeader.equals(actualHeader)) {
            throw new IllegalArgumentException("Unexpected header column " +
                    index + ": " + actualHeader +
                    ", expected: " + expectedHeader);
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
