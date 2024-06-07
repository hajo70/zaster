package de.spricom.zaster.data;

import jakarta.persistence.Entity;
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
@Table(name = "ACCOUNT_CURRENCY")
public class AccountCurrency extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @ToString.Exclude
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Currency currency;
}
