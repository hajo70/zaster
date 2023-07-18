package de.spricom.zaster.importers;

import de.spricom.zaster.security.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class ImportServiceImpl implements ImportService {

    private final AuthenticatedUser authenticatedUser;

    private final CsvImporter[] csvImporters;

    @Override
    @Transactional
    public void importFile(Resource resource, String importerName) {
        CsvImporter importer = getImporter(importerName);
        List<CsvRow> rows = scan(resource, importer);
        importer.process(rows);
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
            throw new IllegalArgumentException("MD5-Hash for " + resource.getFilename());
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
