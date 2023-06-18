package de.spricom.zaster.entities.tracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
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
@Table(name = "ACCOUNT_GROUP")
public class AccountGroupEntity extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AccountGroupEntity parent;

    @OneToMany(mappedBy="accountGroup", fetch = FetchType.LAZY)
    private Set<AccountEntity> accounts;

    private String accountName;

    @JsonIgnore
    @Transient
    private SortedSet<AccountGroupEntity> children;
}
