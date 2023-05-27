package de.spicom.zaster.endpoints.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transfer {
    private Account source;
    private Account target;
    private BigDecimal sourceAmount;
    private BigDecimal targetAmount;

    private String note;
}
