package de.spricom.zaster.endpoints.model;

import java.time.Instant;
import java.util.Set;

public class Booking {
    private Instant at;

    private Set<Transfer> transfers;

    private String note;
}
