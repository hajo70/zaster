package de.spricom.zaster.importing;

import de.spricom.zaster.data.FileSource;
import de.spricom.zaster.data.Import;
import de.spricom.zaster.data.TrackingDateTime;
import de.spricom.zaster.importing.csv.CsvReader;
import de.spricom.zaster.importing.csv.CsvRow;
import de.spricom.zaster.services.ImportService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ImportHandlingService {

    public record Stats(
            int totalCount,
            int importedCount,
            String filename
    ) {
    }

    private final ImportService importService;
    private final CsvImporter[] csvImporters;

    public List<String> getImporterNames() {
        return Arrays.stream(csvImporters).map(CsvImporter::getName).collect(Collectors.toList());
    }

    @Transactional
    public Stats importFile(String importerName, Resource resource) {
        String fileMd5 = md5Hash(resource);
        Optional<FileSource> source = importService.findByMd5(fileMd5);
        if (source.isPresent()) {
            throw new AlreadyImportedException(source.get().getFilename(),
                    source.get().getImported().getImportedAt().toLocalDateTime());
        }
        CsvImporter importer = getImporter(importerName);
        List<CsvRow> rows = scan(resource, importer);
        FileSource fileSource = importService.create(createFileSource(resource, importer, fileMd5));
        CsvImporter.Stats stats = importer.process(fileSource.getImported(), rows);
        return new Stats(stats.totalCount(), stats.importedCount(), resource.getFilename());
    }

    private FileSource createFileSource(Resource resource, CsvImporter importer, String md5) {
        var imported = new Import();
        imported.setImportedAt(TrackingDateTime.now());
        imported.setImporterName(importer.getName());
        var fileSource = new FileSource();
        fileSource.setImported(imported);
        fileSource.setFilename(resource.getFilename());
        fileSource.setMd5(md5);
        return fileSource;
    }

    private List<CsvRow> scan(Resource resource, CsvImporter importer) {
        CsvReader reader = new CsvReader(
                importer.getDelimiter(),
                importer.getCharset()
        );
        try {
            return reader.scan(resource.getInputStream());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Cannot scan '" + resource.getFilename()
                    + "' using " + importer.getDelimiter() + ".");
        }
    }

    private String md5Hash(Resource resource) {
        try {
            return DigestUtils.md5Hex(resource.getInputStream());
        } catch (IOException ex) {
            throw new IllegalArgumentException("MD5-Hash for " + resource.getFilename(), ex);
        }
    }

    private CsvImporter getImporter(String importerName) {
        for (CsvImporter csvImporter : csvImporters) {
            if (importerName.equals(csvImporter.getName())) {
                return csvImporter;
            }
        }
        throw new IllegalArgumentException("There is no CSV importer named '" + importerName + "'.");
    }
}
