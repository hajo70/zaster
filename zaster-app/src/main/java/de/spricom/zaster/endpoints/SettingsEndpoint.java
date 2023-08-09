package de.spricom.zaster.endpoints;

import de.spricom.zaster.entities.management.TenantEntity;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.repository.ManagementService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;

@Endpoint
@RolesAllowed("ADMIN")
@AllArgsConstructor
public class SettingsEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final ManagementService managementService;
    private final CurrencyService currencyService;

    public TenantEntity saveTenant(TenantEntity tenant) {
        if (!tenant.equals(authenticatedUser.getCurrentTenant())) {
            throw new IllegalArgumentException("Only current tenant can be updated");
        }
        return managementService.updateTenant(tenant);
    }
}
