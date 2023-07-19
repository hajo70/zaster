package de.spricom.zaster.importing;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ImportHandlingService {
    List<String> getImporterNames();
    void importFile(Resource resource, String importerName);
}
