package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.currency.CurrencyEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    private String accountName;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CurrencyEntity currency;
}
