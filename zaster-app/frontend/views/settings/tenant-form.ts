import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from 'Frontend/views/view';

import '@vaadin/button';
import '@vaadin/combo-box';
import '@vaadin/text-field';

import {Binder, field} from '@hilla/form';
import {settingsStore} from "Frontend/views/settings/settings-store.ts";
import TenantDtoModel from "Frontend/generated/de/spricom/zaster/dtos/settings/TenantDtoModel.ts";

@customElement('tenant-form')
export class TenantForm extends View {
    protected binder = new Binder(this, TenantDtoModel);

    constructor() {
        super();
        this.autorun(() => {
            if (settingsStore.tenant) {
                this.binder.read(settingsStore.tenant);
            } else {
                this.binder.clear();
            }
        });
    }

    render() {
        const {model} = this.binder;

        return html`
            <vaadin-text-field
                    label="Tenant name"
                    ${field(model.name)}
            ></vaadin-text-field>

            <div class="flex gap-s">
                <vaadin-button theme="primary" @click=${this.save}>
                    Speichern
                </vaadin-button>
            </div>
        `;
    }

    async save() {
        await this.binder.submitTo(settingsStore.saveTenant);
    }
}