import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';
import '@vaadin/multi-select-combo-box';

import {Binder, field} from '@hilla/form';
import AccountDtoModel from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountDtoModel.ts";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

@customElement('account-form')
export class AccountForm extends View {
    protected binder = new Binder(this, AccountDtoModel);

    constructor() {
        super();
        this.autorun(() => {
            if (bookingsViewStore.editedAccount) {
                this.binder.read(bookingsViewStore.editedAccount);
            } else {
                this.binder.clear();
            }
        });
    }

    render() {
        const {model} = this.binder;

        return html`
            <div class="flex gap-s"
                style="--vaadin-field-default-width: 18em">
                <vaadin-text-field
                        class="flex-grow"
                        label="Account Name"
                        ${field(model.accountName)}
                ></vaadin-text-field>

                <vaadin-text-field
                        label="Account Nummer"
                        ${field(model.accountCode)}
                ></vaadin-text-field>
            </div>

            <div class="flex gap-s ml-auto">
                <vaadin-button theme="primary" @click=${this.save}>
                    ${this.binder.value.id ? 'Speichern' : 'Erstellen'}
                </vaadin-button>
                <vaadin-button theme="error" @click=${this.delete}>
                    Löschen
                </vaadin-button>
                <vaadin-button theme="tertiary" @click=${this.onCancel}>
                    Abbrechen
                </vaadin-button>
            </div>
        `;
    }

    onCancel() {
        bookingsViewStore.cancelEdit();
    }

    async save() {
        // await this.binder.submitTo(accountsViewStore.save);
        this.binder.clear();
        this.notifyUpdate();
    }

    async delete() {
        // await accountsViewStore.delete();
        this.notifyUpdate();
    }

    private notifyUpdate() {
        this.dispatchEvent(new CustomEvent("accounts-changed"));
    }
}