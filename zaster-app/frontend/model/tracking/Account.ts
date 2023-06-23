import CurrencyEntity from "Frontend/generated/de/spricom/zaster/entities/currency/CurrencyEntity.ts";
import {AccountGroup} from "Frontend/model/tracking/AccountGroup.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";

export class Account {
    readonly data: AccountDto;
    readonly group: AccountGroup;
    readonly currency: CurrencyEntity;

    constructor(group: AccountGroup, data: AccountDto, currency: CurrencyEntity) {
        this.group = group;
        this.data = data;
        this.currency = currency;
    }

    get currencyCode() {
        return this.currency.currencyCode;
    }
}