package de.spricom.zaster.importers;

import org.springframework.core.io.Resource;

public interface ImportService {
    void importFile(Resource resource, String importerName);
}
