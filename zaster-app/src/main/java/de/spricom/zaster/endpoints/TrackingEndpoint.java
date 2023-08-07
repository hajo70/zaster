package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.tracking.BookingDto;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@Endpoint
@PermitAll
@AllArgsConstructor
public class TrackingEndpoint {

    public List<BookingDto> getTransactions() {
        return Collections.emptyList();
    }
}
