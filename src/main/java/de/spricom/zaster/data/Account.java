package de.spricom.zaster.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Account extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account parent;

    @OneToMany(mappedBy="account", fetch = FetchType.LAZY)
    private Set<AccountCurrency> currencies;

    private String accountName;

    private String accountCode;

    @Lob
    @Column(length = 65536)
    private String metadata;

    @Transient
    private SortedSet<Account> children;
}
