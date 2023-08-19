package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.dtos.settings.CurrencyDto;
import de.spricom.zaster.dtos.settings.TenantDto;
import de.spricom.zaster.dtos.settings.UserDto;
import de.spricom.zaster.dtos.tracking.AccountCurrencyDto;
import de.spricom.zaster.dtos.tracking.AccountDto;
import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.AccountCurrencyEntity;
import de.spricom.zaster.entities.tracking.AccountEntity;

public final class DtoUtils {

    private DtoUtils() {}

    public static IdDto id(AbstractEntity entity) {
        return new IdDto(entity.getId(), entity.getVersion());
    }

    public static void setId(AbstractEntity entity, IdDto id) {
        entity.setId(id.uuid());
        entity.setVersion(id.version());
    }

    public static UserDto toUserDto(UserEntity user) {
        return new UserDto(
                DtoUtils.id(user),
                user.getUsername(),
                user.getName(),
                null,
                user.getUserRoles()
        );
    }

    public static TenantDto toTenantDto(TenantEntity tenant) {
        return new TenantDto(
                DtoUtils.id(tenant),
                tenant.getName(),
                tenant.getLocale(),
                tenant.getTimezone()
        );
    }

    public static CurrencyDto toCurrencyDto(CurrencyEntity currency) {
        return new CurrencyDto(
                DtoUtils.id(currency),
                currency.getCurrencyCode(),
                currency.getCurrencyName(),
                currency.getCurrencyType()
        );
    }

    public static AccountDto toAccountDto(AccountEntity entity) {
        return new AccountDto(
                DtoUtils.id(entity),
                entity.getAccountName(),
                entity.getCurrencies() == null || entity.getCurrencies().isEmpty()
                        ? null
                        : entity.getCurrencies().stream()
                        .map(DtoUtils::toAccountCurrencyDto)
                        .toList(),
                entity.getParent() == null ? null : entity.getParent().getId(),
                entity.getChildren() == null || entity.getChildren().isEmpty()
                        ? null
                        : entity.getChildren().stream()
                        .map(DtoUtils::toAccountDto)
                        .toList()
        );
    }

    public static AccountCurrencyDto toAccountCurrencyDto(AccountCurrencyEntity entity) {
        return new AccountCurrencyDto(
                DtoUtils.id(entity),
                entity.getCurrency().getId()
        );
    }
}
