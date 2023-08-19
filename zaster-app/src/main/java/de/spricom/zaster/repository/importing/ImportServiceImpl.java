package de.spricom.zaster.repository.importing;

import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.FileSourceEntity;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.ImportService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class ImportServiceImpl implements ImportService {

    private final ImportRepository importRepository;
    private final FileSourceRepository fileSourceRepository;
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;

    @Override
    public Optional<FileSourceEntity> findByMd5(TenantEntity tenant, String md5) {
        return fileSourceRepository.findByMd5(tenant.getId(), md5);
    }

    @Override
    public FileSourceEntity create(FileSourceEntity fileSource) {
        fileSource.setImported(importRepository.save(fileSource.getImported()));
        return fileSourceRepository.save(fileSource);
    }
}
