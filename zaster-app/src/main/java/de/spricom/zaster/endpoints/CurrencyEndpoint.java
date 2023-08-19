package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.settings.CurrencyDto;
import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;

@Endpoint
@PermitAll
@AllArgsConstructor
public class CurrencyEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final CurrencyService currencyService;

    public CurrencyDto saveCurrency(CurrencyDto currency) {
        return DtoUtils.toCurrencyDto(currencyService.saveCurrency(toCurrencyEntity(currency)));
    }

    private CurrencyEntity toCurrencyEntity(CurrencyDto dto) {
        var entity = new CurrencyEntity();
        DtoUtils.setId(entity, dto.id());
        entity.setTenant(authenticatedUser.getCurrentTenant());
        entity.setCurrencyCode(dto.currencyCode().toUpperCase());
        entity.setCurrencyName(dto.currencyName());
        entity.setCurrencyType(dto.currencyType());
        return entity;
    }

    public void deleteCurrencyById(String currencyId) {
        currencyService.deleteCurrencyById(currencyId);
    }
}
