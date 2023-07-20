package de.spricom.zaster.importing;

import de.spricom.zaster.entities.managment.TenantEntity;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ImportHandlingService {
    List<String> getImporterNames();
    void importFile(TenantEntity tenant, String importerName, Resource resource);
}
