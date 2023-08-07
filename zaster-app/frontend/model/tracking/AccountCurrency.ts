import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import AccountCurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountCurrencyDto.ts";

export class AccountCurrency {
    readonly data: AccountCurrencyDto;
    readonly group: Account;
    readonly currency: CurrencyEntity;

    constructor(group: Account, data: AccountCurrencyDto, currency: CurrencyEntity) {
        this.group = group;
        this.data = data;
        this.currency = currency;
    }

    get currencyCode() {
        return this.currency.currencyCode;
    }
}