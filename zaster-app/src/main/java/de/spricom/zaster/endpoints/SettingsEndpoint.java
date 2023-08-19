package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.settings.TenantDto;
import de.spricom.zaster.entities.settings.TenantEntity;
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

    public TenantDto saveTenant(TenantDto tenant) {
        return DtoUtils.toTenantDto(managementService.updateTenant(toCurrencyEntity(tenant)));
    }

    private TenantEntity toCurrencyEntity(TenantDto dto) {
        var entity = new TenantEntity();
        DtoUtils.setId(entity, dto.id());
        entity.setName(dto.name());
        entity.setLocale(dto.locale());
        entity.setTimezone(dto.timeZone());
        return entity;
    }
}
