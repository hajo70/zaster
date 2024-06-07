package de.spricom.zaster.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "TRANSFER", uniqueConstraints = @UniqueConstraint(columnNames = {"TRANSFERRED_AT_DATE", "SERIAL"}))
public class Transfer extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountCurrency accountCurrency;

    /**
     * The position of the transfer within a booking starting at 1.
     */
    private int position;

    /**
     * The order of the transfer on that day starting at 1.
     */
    private Integer serial;

    @Embedded
    private TrackingDateTime transferredAt;

    @Column(precision = 40, scale = 15)
    private BigDecimal amount;

    @Column(precision = 40, scale = 15)
    private BigDecimal balance;
}
