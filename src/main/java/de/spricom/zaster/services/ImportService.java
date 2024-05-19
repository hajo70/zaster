package de.spricom.zaster.services;

import de.spricom.zaster.data.FileSource;
import de.spricom.zaster.data.FileSourceRepository;
import de.spricom.zaster.data.ImportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class ImportService {

    private final ImportRepository importRepository;
    private final FileSourceRepository fileSourceRepository;
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final BookingService bookingService;

    public Optional<FileSource> findByMd5(String md5) {
        return fileSourceRepository.findByMd5(md5);
    }

    public FileSource create(FileSource fileSource) {
        fileSource.setImported(importRepository.save(fileSource.getImported()));
        return fileSourceRepository.save(fileSource);
    }
}
