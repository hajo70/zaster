package de.spricom.zaster.entities.tracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.management.TenantEntity;
import jakarta.persistence.*;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AccountEntity parent;

    @OneToMany(mappedBy="account", fetch = FetchType.LAZY)
    private Set<AccountCurrencyEntity> currencies;

    private String accountName;

    private String accountCode;

    @JsonIgnore
    @Lob
    @Column(length = 65536)
    private String metadata;

    @JsonIgnore
    @Transient
    private SortedSet<AccountEntity> children;
}
