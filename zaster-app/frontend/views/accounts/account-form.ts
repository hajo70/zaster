import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';
import '@vaadin/multi-select-combo-box';

import {Binder, field} from '@hilla/form';
import {accountsViewStore} from "Frontend/views/accounts/accounts-view-store.ts";
import AccountGroupDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountGroupDtoModel.ts";

@customElement('account-form')
export class AccountForm extends View {
    protected binder = new Binder(this, AccountGroupDtoModel);

    constructor() {
        super();
        this.autorun(() => {
            if (accountsViewStore.selectedAccountGroup) {
                this.binder.read(accountsViewStore.selectedAccountGroup);
            } else {
                this.binder.clear();
            }
        });
    }

    render() {
        const {model} = this.binder;

        return html`
            <vaadin-combo-box
                    label="Parent"
                    item-label-path="accountName"
                    item-value-path="id.id"
                    .items="${accountsViewStore.allAccountGroups}"
                    ${field(model.parentId)}
                    clear-button-visible
            ></vaadin-combo-box>
            
            <vaadin-text-field
                    label="Konto-Name"
                    ${field(model.accountName)}
            ></vaadin-text-field>

            <vaadin-multi-select-combo-box
                    label="Currencies"
                    .items="${accountsViewStore.currencyCodes}"
            ></vaadin-multi-select-combo-box>
            
            <div class="flex gap-s">
                <vaadin-button theme="primary" @click=${this.save}>
                    ${this.binder.value.id ? 'Speichern' : 'Erstellen'}
                </vaadin-button>
                <vaadin-button theme="error" @click=${this.delete}>
                    Löschen
                </vaadin-button>
                <vaadin-button theme="tertiary" @click=${accountsViewStore.cancelEdit}>
                    Abbrechen
                </vaadin-button>
            </div>
        `;
    }

    async save() {
        await this.binder.submitTo(accountsViewStore.save);
        this.binder.clear();
        this.notifyUpdate();
    }

    async delete() {
        await accountsViewStore.delete();
        this.notifyUpdate();
    }

    private notifyUpdate() {
        this.dispatchEvent(new CustomEvent("accounts-changed"));
    }
}