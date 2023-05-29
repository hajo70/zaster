package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "BOOKING")
public class BookingEntity extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private TransactionEntity transaction;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountEntity account;

    @Embedded
    @AttributeOverride(name = "date", column = @Column(name="BOOKED_AT_DATE"))
    @AttributeOverride(name = "zonedDateTime", column = @Column(name="BOOKED_AT_TS"))
    private TrackingDateTime bookedAt;

    @Column(precision = 40, scale = 15)
    private BigDecimal amount;
}
