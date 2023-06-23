package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.dtos.tracking.TransactionDto;
import dev.hilla.Endpoint;

import java.util.Collections;
import java.util.List;

@Endpoint
@AnonymousAllowed
public class TrackingEndpoint {

    public List<TransactionDto> getTransactions() {
        return Collections.emptyList();
    }
}
