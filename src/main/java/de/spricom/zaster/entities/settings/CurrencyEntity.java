package de.spricom.zaster.entities.settings;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.enums.tracking.CurrencyType;
import de.spricom.zaster.enums.tracking.ZasterCurrency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CURRENCY")
public class CurrencyEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    @Column(length = 8)
    private String currencyCode;

    @Column(length = 64)
    private String currencyName;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ZasterCurrency zasterCurrency;
}
