package de.spricom.zaster.endpoints;

import de.spricom.zaster.importing.ImportHandlingService;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Endpoint
@PermitAll
@AllArgsConstructor
@Log4j2
public class ImportEndpoint {

    private final ImportHandlingService importHandlingService;

    public @Nonnull List<@Nonnull String> getImporterNames() {
        return importHandlingService.getImporterNames();
    }
}
