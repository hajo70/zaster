package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT")
@AttributeOverride(name = "id", column = @Column(name = "ACCOUNT_ID"))
public class AccountEntity extends AbstractEntity {

    private String accountName;
    @ManyToOne
    @JoinColumn(name = "CURRENCY_ID", insertable = false, updatable = false, nullable = false)
    private CurrencyEntity currency;
}
