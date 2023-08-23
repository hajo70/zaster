import {Account} from "Frontend/model/tracking/Account.ts";
import AccountCurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountCurrencyDto.ts";
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import SnapshotDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/SnapshotDto.ts";

export class AccountCurrency {
    readonly data: AccountCurrencyDto;
    readonly account: Account;
    readonly currency: CurrencyDto;
    snapshots: SnapshotDto[];

    constructor(account: Account, data: AccountCurrencyDto, currency: CurrencyDto) {
        this.account = account;
        this.data = data;
        this.currency = currency;
        this.snapshots = [];
    }

    get currencyCode() {
        return this.currency.currencyCode;
    }
}