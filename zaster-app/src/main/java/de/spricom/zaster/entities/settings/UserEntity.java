package de.spricom.zaster.entities.settings;

import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.enums.settings.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "USERZ")
public class UserEntity extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private TenantEntity tenant;

    @Column(unique = true)
    private String username;
    private String name;
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"))
    private Set<UserRole> userRoles;
}
