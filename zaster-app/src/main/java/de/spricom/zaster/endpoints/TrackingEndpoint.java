package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.tracking.TrackingDataDto;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;

import java.util.Collections;

@Endpoint
@PermitAll
@AllArgsConstructor
public class TrackingEndpoint {

    public TrackingDataDto loadTrackingData() {
        return new TrackingDataDto(
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
