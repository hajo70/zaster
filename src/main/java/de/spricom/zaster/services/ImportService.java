package de.spricom.zaster.services;

import de.spricom.zaster.data.FileSource;
import de.spricom.zaster.data.FileSourceRepository;
import de.spricom.zaster.data.Import;
import de.spricom.zaster.data.ImportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class ImportService {

    private final ImportRepository importRepository;
    private final FileSourceRepository fileSourceRepository;

    public Page<Import> list(Pageable pageable) {
        return importRepository.findAll(pageable);
    }

    public Page<Import> list(Pageable pageable, Specification<Import> filter) {
        return importRepository.findAll(filter, pageable);
    }

    public Optional<Import> getImport(String importId) {
        return importRepository.findById(importId);
    }

    public Optional<FileSource> findByMd5(String md5) {
        return fileSourceRepository.findByMd5(md5);
    }

    public FileSource create(FileSource fileSource) {
        fileSource.setImported(importRepository.save(fileSource.getImported()));
        return fileSourceRepository.save(fileSource);
    }

    public FileSource update(FileSource fileSource) {
        fileSource.setImported(importRepository.save(fileSource.getImported()));
        return fileSourceRepository.save(fileSource);
    }
}
