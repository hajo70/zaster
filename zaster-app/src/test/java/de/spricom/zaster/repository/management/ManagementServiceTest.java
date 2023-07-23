package de.spricom.zaster.repository.management;

import de.spricom.zaster.entities.managment.ApplicationUserEntity;
import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.managment.UserRole;
import de.spricom.zaster.repository.ManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ManagementServiceTest {

    @Autowired
    private ManagementService managementService;

    @Test
    void testCreateTenant() {
        var tenant = new TenantEntity();
        tenant.setName("My fancy tenant");

        var user = new ApplicationUserEntity();
        user.setUserRoles(EnumSet.of(UserRole.USER, UserRole.ADMIN));
        user.setName("Max Testuser");
        user.setUsername("mgmttestuser");
        user.setHashedPassword("abcdef");
        user.setTenant(tenant);

        var savedUser = managementService.createTenant(user);
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getTenant().getId()).isNotNull();
        assertThat(savedUser)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);

        var loggedInUser = managementService.findByUsername(user.getUsername()).get();
        assertThat(loggedInUser)
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }
}