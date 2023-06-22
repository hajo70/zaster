import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';
import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';
import {Binder, field} from '@hilla/form';
import {currenciesViewStore} from "Frontend/views/currencies/currencies-view-store";
import AccountGroupModel from "Frontend/generated/de/spricom/zaster/endpoints/AccountGroupModel.ts";
import {accountsViewStore} from "Frontend/views/accounts/accounts-view-store.ts";

@customElement('account-form')
export class AccountForm extends View {
    protected binder = new Binder(this, AccountGroupModel);

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
            <vaadin-text-field
                    label="Konto-Name"
                    ${field(model.accountName)}
            ></vaadin-text-field>

            <div class="flex gap-s">
                <vaadin-button theme="primary" @click=${this.save}>
                    ${this.binder.value.id ? 'Speichern' : 'Erstellen'}
                </vaadin-button>
                <vaadin-button theme="error" @click=${accountsViewStore.delete}>
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
    }
}