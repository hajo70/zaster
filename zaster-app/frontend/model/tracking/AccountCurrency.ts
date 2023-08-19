import {Account} from "Frontend/model/tracking/Account.ts";
import AccountCurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountCurrencyDto.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";

export class AccountCurrency {
    readonly data: AccountCurrencyDto;
    readonly account: Account;
    readonly currency: CurrencyDto;

    constructor(account: Account, data: AccountCurrencyDto, currency: CurrencyDto) {
        this.account = account;
        this.data = data;
        this.currency = currency;
    }

    get currencyCode() {
        return this.currency.currencyCode;
    }
}