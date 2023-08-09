package de.spricom.zaster.importing;

import de.spricom.zaster.entities.management.TenantEntity;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ImportHandlingService {
    List<String> getImporterNames();
    Stats importFile(TenantEntity tenant, String importerName, Resource resource);

    record Stats(
            int totalCount,
            int importedCount,
            String filename
    ) {
    }
}
