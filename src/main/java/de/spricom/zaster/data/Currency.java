package de.spricom.zaster.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CURRENCY")
public class Currency extends AbstractEntity {

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
