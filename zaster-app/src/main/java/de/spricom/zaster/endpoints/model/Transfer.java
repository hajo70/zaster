package de.spricom.zaster.endpoints.model;

import java.math.BigDecimal;

public class Transfer {
    private Account source;
    private Account target;
    private BigDecimal sourceAmount;
    private BigDecimal targetAmount;

    private String note;
}
