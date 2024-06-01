package de.spricom.zaster.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "TRANSFER")
public class Transfer extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountCurrency accountCurrency;

    @Embedded
    private TrackingDateTime transferredAt;

    @Column(precision = 40, scale = 15)
    private BigDecimal amount;
}
