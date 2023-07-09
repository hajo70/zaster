import {customElement} from "lit/decorators.js";
import {html} from "lit";

import '@vaadin/upload';

import {View} from "Frontend/views/view.ts";

@customElement("import-view")
export class ImportView extends View {

    protected override render() {
        return html`
            <div>
                <h1>Import Transactions</h1>
                <vaadin-upload
                        target="/api/upload-handler"
                        headers='{ "X-API-KEY": "7f4306cb-bb25-4064-9475-1254c4eff6e5" }'>
                </vaadin-upload>
            </div>
        `;
    }

    async connectedCallback() {
        super.connectedCallback();
        this.classList.add(
            'box-border',
            'flex',
            'flex-col',
            'p-m',
            'gap-s',
            'w-full',
            'h-full'
        );
    }

}