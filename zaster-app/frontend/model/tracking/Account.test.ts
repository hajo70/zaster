import {expect, test} from 'vitest'
import CurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/settings/CurrencyDto.ts";
import IdDto from "Frontend/generated/de/spricom/zaster/dtos/common/IdDto.ts";
import CurrencyType from "Frontend/generated/de/spricom/zaster/enums/tracking/CurrencyType.ts";
import AccountDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDto.ts";
import {Account} from "Frontend/model/tracking/Account.ts";
import AccountCurrencyDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountCurrencyDto.ts";

const someDate = '2023-08-17';

const currencyDto: CurrencyDto = {
    id: id(1),
    currencyCode: "EUR",
    currencyName: "Euro",
    currencyType: CurrencyType.ISO_4217
}

function id(n: number): IdDto {
    return {
        uuid: "" + n,
        version: 0
    }
}

test('creates empty root account', () => {
    const dto: AccountDto = {
        id: id(10),
        accountName: "empty root account"
    }
    const account = new Account(null, dto, (id) => currencyDto);
    expect(account.data.id.uuid).toBe("10");
    expect(account.parent).toBe(null);
    expect(account.name).toBe("empty root account");
    expect(account.lookUpAccountCurrency("11")).toBe(undefined);
})

test('creates hierarchy', () => {
    const ac: AccountCurrencyDto = {
        id: id(20),
        currencyId: "1"
    }
    const child_1_1: AccountDto = {
        id: id(10),
        accountName: "child_1_1",
        currencies: [ac]
    }
    const child_1: AccountDto = {
        id: id(11),
        accountName: "child_1",
        children: [child_1_1]
    }
    const child_2: AccountDto = {
        id: id(12),
        accountName: "child_2",
    }
    const parent: AccountDto = {
        id: id(13),
        accountName: "parent",
        children: [child_1, child_2]
    }
    const account = new Account(null, parent, (id) => currencyDto);
    expect(account.data.id.uuid).toBe("13");
    expect(account.parent).toBe(null);
    expect(account.name).toBe("parent");
    expect(account.children).toHaveLength(2);
    expect(account.children[0].name).toBe("child_1")
    expect(account.children[1].name).toBe("child_2")
    expect(account.children[0].children[0].name).toBe("child_1_1");
    expect(account.children[0].parent).toBe(account);
    expect(account.children[0].children[0].parent?.parent).toBe(account);
    expect(account.lookUpAccountCurrency("21")).toBe(undefined);
    const accountCurrency = account.lookUpAccountCurrency("20");
    expect(accountCurrency?.data).toBe(ac);
    expect(accountCurrency?.currencyCode).toBe("EUR");
    expect(accountCurrency?.account.data).toBe(child_1_1);
})
