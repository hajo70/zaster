package de.spricom.zaster.entities.currency;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import jakarta.persistence.*;
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

    @Column(length = 63)
    private String currencyName;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private ZasterCurrency zasterCurrency;
}
