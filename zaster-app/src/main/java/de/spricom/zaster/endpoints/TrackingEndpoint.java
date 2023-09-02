package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.tracking.TrackingDataDto;
import de.spricom.zaster.repository.BookingService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;

@Endpoint
@PermitAll
@AllArgsConstructor
public class TrackingEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final BookingService bookingService;

    public TrackingDataDto loadTrackingData() {
        var tenant = authenticatedUser.getCurrentTenant();
        return new TrackingDataDto(
                bookingService.loadAllBookings(tenant).stream().map(DtoUtils::toBookingDto).toList(),
                bookingService.loadAllSnapshots(tenant).stream().map(DtoUtils::toSnapshotDto).toList()
        );
    }
}
