package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.dtos.common.TrackingDateTimeDto;
import de.spricom.zaster.dtos.settings.CurrencyDto;
import de.spricom.zaster.dtos.settings.TenantDto;
import de.spricom.zaster.dtos.settings.UserDto;
import de.spricom.zaster.dtos.tracking.*;
import de.spricom.zaster.entities.common.AbstractEntity;
import de.spricom.zaster.entities.common.TrackingDateTime;
import de.spricom.zaster.entities.settings.CurrencyEntity;
import de.spricom.zaster.entities.settings.TenantEntity;
import de.spricom.zaster.entities.settings.UserEntity;
import de.spricom.zaster.entities.tracking.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

public final class DtoUtils {

    private DtoUtils() {}

    public static IdDto id(AbstractEntity entity) {
        return new IdDto(entity.getId(), entity.getVersion());
    }

    public static void setId(AbstractEntity entity, IdDto id) {
        entity.setId(id.uuid());
        entity.setVersion(id.version());
    }

    public static TrackingDateTimeDto ts(TrackingDateTime ts) {
        return new TrackingDateTimeDto(
                timeStamp(ts),
                ts.getDate(),
                ts.getTime(),
                Optional.ofNullable(ts.getOffset()).map(ZoneOffset::toString).orElse(null),
                Optional.ofNullable(ts.getZone()).map(ZoneId::toString).orElse(null)
        );
    }

    private static Instant timeStamp(TrackingDateTime ts) {
        return ts.toZonedDateTime().toInstant();
    }

    public static UserDto toUserDto(UserEntity user) {
        return new UserDto(
                id(user),
                user.getUsername(),
                user.getName(),
                null,
                user.getUserRoles()
        );
    }

    public static TenantDto toTenantDto(TenantEntity tenant) {
        return new TenantDto(
                id(tenant),
                tenant.getName(),
                tenant.getLocale(),
                tenant.getTimezone()
        );
    }

    public static CurrencyDto toCurrencyDto(CurrencyEntity currency) {
        return new CurrencyDto(
                id(currency),
                currency.getCurrencyCode(),
                currency.getCurrencyName(),
                currency.getCurrencyType()
        );
    }

    public static AccountDto toAccountDto(AccountEntity entity) {
        return new AccountDto(
                id(entity),
                entity.getAccountCode(),
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
                id(entity),
                entity.getCurrency().getId()
        );
    }

    public static BookingDto toBookingDto(BookingEntity entity) {
        return new BookingDto(
                id(entity),
                ts(entity.getBookedAt()),
                entity.getDescription(),
                entity.getTransfers().stream().map(DtoUtils::toTransferDto).toList()
        );
    }

    public static TransferDto toTransferDto(TransferEntity entity) {
        return new TransferDto(
                id(entity),
                entity.getAccountCurrency().getId(),
                entity.getAmount(),
                DtoUtils.ts(entity.getTransferredAt())
        );
    }

    public static SnapshotDto toSnapshotDto(SnapshotEntity entity) {
        return new SnapshotDto(
                id(entity),
                entity.getAccountCurrency().getId(),
                entity.getBalance(),
                DtoUtils.ts(entity.getTakenAt())
        );
    }
}
