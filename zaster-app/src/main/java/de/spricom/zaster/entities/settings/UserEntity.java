package de.spricom.zaster.entities.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.enums.settings.UserRole;
import dev.hilla.Nonnull;
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

    @Nonnull
    @Column(unique = true)
    private String username;
    @Nonnull
    private String name;
    @JsonIgnore
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"))
    @Nonnull
    private Set<UserRole> userRoles;
}
