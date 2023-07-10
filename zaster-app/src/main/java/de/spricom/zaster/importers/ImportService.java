package de.spricom.zaster.importers;

import de.spricom.zaster.security.AuthenticatedUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImportService {

    private final AuthenticatedUser authenticatedUser;


}
