package de.spricom.zaster.repository.importing;

import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class ImportServiceImpl {

    private final ImportRepository importRepository;
    private final FileSourceRepository fileSourceRepository;
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;


}
