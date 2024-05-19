package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.settings.CurrencyEntity;
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
@Table(name = "ACCOUNT_CURRENY")
public class AccountCurrencyEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @ToString.Exclude
    private AccountEntity account;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CurrencyEntity currency;
}
