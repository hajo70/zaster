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
@Table(name = "SNAPSHOT")
public class SnapshotEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountCurrencyEntity accountCurrency;

    @Embedded
    private TrackingDateTime takenAt;

    @Column(precision = 40, scale = 15)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private ImportEntity imported;
}
