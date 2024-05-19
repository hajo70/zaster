package de.spricom.zaster.entities.tracking;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.SortedSet;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AccountEntity parent;

    @OneToMany(mappedBy="account", fetch = FetchType.LAZY)
    private Set<AccountCurrencyEntity> currencies;

    private String accountName;

    private String accountCode;

    @Lob
    @Column(length = 65536)
    private String metadata;

    @Transient
    private SortedSet<AccountEntity> children;
}
