package de.spricom.zaster.entities.managment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.spricom.zaster.entities.common.AbstractEntity;
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
@Table(name = "APPLICATION_USER")
public class ApplicationUserEntity extends AbstractEntity {

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
    @CollectionTable(name = "APPLICATION_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"))
    @Nonnull
    private Set<UserRole> userRoles;
}
