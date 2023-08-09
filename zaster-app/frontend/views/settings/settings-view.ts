import {View} from "Frontend/views/view.ts";
import {customElement} from "lit/decorators.js";
import {html} from "lit";

import '@vaadin/tabsheet';
import '@vaadin/tabs';
import './tenant-form';

@customElement("settings-view")
export class SettingsView extends View {

    render() {
        return html`
            <div>
                <vaadin-tabsheet>
                    <vaadin-tabs slot="tabs">
                        <vaadin-tab id="tenant-tab">Tenant</vaadin-tab>
                        <vaadin-tab id="users-tab">Users</vaadin-tab>
                        <vaadin-tab id="currencies-tab">Currencies</vaadin-tab>
                        <vaadin-tab id="export-tab">Import / Export</vaadin-tab>
                    </vaadin-tabs>

                    <tenant-form tab="tenant-tab"></tenant-form>
                    <div tab="users-tab">This is the Users tab content</div>
                    <div tab="currencies-tab">This is the Currencies tab content</div>
                    <div tab="export-tab">This is the Import / Export tab content</div>
                </vaadin-tabsheet>
            </div>`;
    }

    connectedCallback() {
        super.connectedCallback();
        this.classList.add(
            'h-full'
        );
    }
}