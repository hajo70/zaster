package de.spricom.zaster.dtos.tracking;

import java.util.List;

public record TrackingDataDto(
        List<BookingDto> bookings,
        List<SnapshotDto> snapshots
) {
}
