package de.spricom.zaster.importing;

import java.time.LocalDateTime;

public class AlreadyImportedException extends RuntimeException {
    private final String filename;
    private final LocalDateTime importedAt;

    public AlreadyImportedException(String filename, LocalDateTime importedAt) {
        super("File " + filename + " already imported at " + importedAt + ".");
        this.filename = filename;
        this.importedAt = importedAt;
    }
}
