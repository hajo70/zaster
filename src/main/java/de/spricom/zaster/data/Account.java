package de.spricom.zaster.data;

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
public class Account extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account parent;

    @OneToMany(mappedBy="account", fetch = FetchType.LAZY)
    private Set<AccountCurrency> currencies;

    private String accountName;

    private String accountCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private AccountType accountType;

    @Lob
    @Column(length = 65536)
    private String metadata;

    @Transient
    private SortedSet<Account> children;
}
