package de.spricom.zaster.importers;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
@PermitAll
@AllArgsConstructor
@Log4j2
public class ImportEndpoint {

    private final CsvImporter[] csvImporters;

    public @Nonnull List<@Nonnull String> getImporterNames() {
        return Arrays.stream(csvImporters).map(CsvImporter::getName).collect(Collectors.toList());
    }
}
