package de.spricom.zaster.importing;

import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.tracking.FileSourceEntity;
import de.spricom.zaster.entities.tracking.ImportEntity;
import de.spricom.zaster.importing.csv.CsvReader;
import de.spricom.zaster.importing.csv.CsvRow;
import de.spricom.zaster.repository.ImportService;
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
public class ImportHandlingServiceImpl implements ImportHandlingService {

    private final ImportService importService;
    private final CsvImporter[] csvImporters;

    @Override
    public List<String> getImporterNames() {
        return Arrays.stream(csvImporters).map(CsvImporter::getName).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Stats importFile(TenantEntity tenant, String importerName, Resource resource) {
        String fileMd5 = md5Hash(resource);
        Optional<FileSourceEntity> source = importService.findByMd5(tenant, fileMd5);
        if (source.isPresent()) {
            throw new AlreadyImportedException(source.get().getFilename(),
                    source.get().getImported().getImportedAt().toLocalDateTime());
        }
        CsvImporter importer = getImporter(importerName);
        List<CsvRow> rows = scan(resource, importer);
        FileSourceEntity fileSource = importService.create(createFileSource(tenant, resource, importer, fileMd5));
        CsvImporter.Stats stats = importer.process(fileSource.getImported(), rows);
        return new Stats(stats.totalCount(), stats.importedCount(), resource.getFilename());
    }

    private FileSourceEntity createFileSource(TenantEntity tenant, Resource resource, CsvImporter importer, String md5) {
        var imported = new ImportEntity();
        imported.setTenant(tenant);
        imported.setImportedAt(TrackingDateTime.now());
        imported.setImporterName(importer.getName());
        var fileSource = new FileSourceEntity();
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
