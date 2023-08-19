package de.spricom.zaster.repository.settings;

import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.enums.settings.UserRole;
import de.spricom.zaster.repository.SettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SettingsServiceTest {

    @Autowired
    private SettingsService settingsService;

    @Test
    void testCreateTenant() {
        var tenant = new TenantEntity();
        tenant.setName("My fancy tenant");

        var user = new UserEntity();
        user.setUserRoles(EnumSet.of(UserRole.USER, UserRole.ADMIN));
        user.setName("Max Testuser");
        user.setUsername("mgmttestuser");
        user.setHashedPassword("abcdef");
        user.setTenant(tenant);

        var savedUser = settingsService.createTenant(user);
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getTenant().getId()).isNotNull();
        assertThat(savedUser)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);

        var loggedInUser = settingsService.findByUsername(user.getUsername()).get();
        assertThat(loggedInUser)
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }
}