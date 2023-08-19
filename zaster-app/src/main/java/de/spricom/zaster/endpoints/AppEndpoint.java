package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.dtos.app.UserInfoDto;
import de.spricom.zaster.dtos.settings.CurrencyDto;
import de.spricom.zaster.dtos.settings.LocaleDto;
import de.spricom.zaster.dtos.settings.TimezoneDto;
import de.spricom.zaster.dtos.tracking.AccountDto;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.enums.tracking.CurrencyType;
import de.spricom.zaster.repository.AccountService;
import de.spricom.zaster.repository.CurrencyService;
import de.spricom.zaster.security.AuthenticatedUser;
import dev.hilla.Endpoint;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@Endpoint
@AnonymousAllowed
@AllArgsConstructor
@Log4j2
public class AppEndpoint {

    private final AuthenticatedUser authenticatedUser;
    private final AccountService accountService;
    private final CurrencyService currencyService;

    public Optional<UserInfoDto> getUserInfo() {
        return authenticatedUser.get().map(this::toUserInfo);
    }

    private UserInfoDto toUserInfo(UserEntity user) {
        log.info("Loading user info for {}.", user.getUsername());
        var tenant = user.getTenant();
        return new UserInfoDto(
                DtoUtils.toUserDto(user),
                DtoUtils.toTenantDto(tenant),
                loadCurrencies(tenant),
                loadRootAccounts(tenant),
                getAvailableLocales(),
                getAvailableTimezones(tenant.getLocale()),
                getAvailableCurrencies(tenant.getLocale())
        );
    }

    private List<CurrencyDto> loadCurrencies(TenantEntity tenant) {
        return currencyService.findAllCurrencies(tenant).stream()
                .map(DtoUtils::toCurrencyDto)
                .toList();
    }

    private List<AccountDto> loadRootAccounts(TenantEntity tenant) {
        return accountService.findAllRootAccounts(tenant)
                .stream().map(DtoUtils::toAccountDto)
                .toList();
    }

    private List<LocaleDto> getAvailableLocales() {
        return Arrays.stream(Locale.getAvailableLocales())
                .map(this::toLocale)
                .toList();
    }

    private LocaleDto toLocale(Locale locale) {
        return new LocaleDto(
                locale,
                locale.getDisplayName(locale)
        );
    }

    private List<TimezoneDto> getAvailableTimezones(Locale locale) {
        return ZoneId.getAvailableZoneIds().stream()
                .map(zoneId -> toTimezone(zoneId, locale))
                .toList();
    }

    private TimezoneDto toTimezone(String zoneId, Locale locale) {
        return new TimezoneDto(
                zoneId,
                ZoneId.of(zoneId).getDisplayName(TextStyle.FULL_STANDALONE, locale)
        );
    }

    private List<CurrencyDto> getAvailableCurrencies(Locale locale) {
        return Currency.getAvailableCurrencies().stream()
                .map(currency -> toCurrency(currency, locale))
                .toList();
    }

    private CurrencyDto toCurrency(Currency currency, Locale locale) {
        return new CurrencyDto(
                null,
                currency.getCurrencyCode(),
                currency.getDisplayName(locale),
                CurrencyType.ISO_4217
        );
    }
}
