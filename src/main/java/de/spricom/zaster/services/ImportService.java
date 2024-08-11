package de.spricom.zaster.services;

import de.spricom.zaster.data.FileSource;
import de.spricom.zaster.data.FileSourceRepository;
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

    public Page<FileSource> list(Pageable pageable) {
        return fileSourceRepository.findAll(pageable);
    }

    public Page<FileSource> list(Pageable pageable, Specification<FileSource> filter) {
        return fileSourceRepository.findAll(filter, pageable);
    }

    public Optional<FileSource> getFileSource(String fileSourceId) {
        return fileSourceRepository.findById(fileSourceId);
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
