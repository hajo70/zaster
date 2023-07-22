package de.spricom.zaster.importing;

import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.importing.csv.CsvReader;
import de.spricom.zaster.importing.csv.CsvRow;
import de.spricom.zaster.importing.importers.PostbankImporter;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class PostbankImporterTest {

    @Mock
    private CurrencyService currencyService;
    @Mock
    private AccountService accountService;
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private PostbankImporter importer;

    @Test
    void testProcess() throws IOException {
        when(currencyService.getOrCreateCurrencyByCode(any(), any())).thenReturn(createCurrency());
        when(accountService.getOrCreateAccount(any(), any(), any(), any())).thenReturn(createAccount());
        var csv = scan(new File("/Users/hajo/Desktop/Konto/Postbank/Kontoumsaetze_210_5140645_00_20230709_134540.csv"));
        importer.process(createImported(), csv);
    }

    private List<CsvRow> scan(File file) throws IOException {
        CsvReader csvReader = new CsvReader(";", StandardCharsets.UTF_8);
        try (InputStream is = new FileInputStream(file)) {
            return csvReader.scan(is);
        }
    }

    private ImportEntity createImported() {
        var imported = new ImportEntity();
        imported.setTenant(new TenantEntity());
        return imported;
    }

    private CurrencyEntity createCurrency() {
        var currency = new CurrencyEntity();
        currency.setCurrencyCode("XYZ");
        return currency;
    }

    private AccountEntity createAccount() {
        var account = new AccountEntity();
        return account;
    }
}