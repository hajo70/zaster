import {customElement, state} from "lit/decorators.js";
import {html} from "lit";
import Cookies from 'js-cookie';

import '@vaadin/combo-box';
import '@vaadin/upload';

import {View} from "Frontend/views/view.ts";
import {ImportEndpoint} from "Frontend/generated/endpoints.ts";

@customElement("import-view")
export class ImportView extends View {

    @state()
    private importers: string[] = [];

    @state()
    private cookies = Cookies.get();

    @state()
    private header: string = '';
    @state()
    private token: string = '';

    protected override render() {
        return html`
            <div>
                <h1>Import Transactions</h1>
                <vaadin-combo-box
                        label="Importers"
                        .items="${this.importers}"
                ></vaadin-combo-box>
                <vaadin-upload
                        target="/api/upload-handler"
                        headers='{ "X-XSRF-TOKEN": "${this.cookies['XSRF-TOKEN']}" }'>
                </vaadin-upload>
            </div>
            <div>
                ${JSON.stringify(this.cookies)}
            </div>
            <hr />
            <div>
                <p>JESSIONID: ${this.cookies['JESSIONID']}</p>
                <p>CSRF-Cookie: ${this.cookies['XSRF-TOKEN']}</p>
                <p>CSRF-Header: ${this.header}</p>
                <p>CSRF-Token: ${this.token}</p>
            </div>
        `;
    }

    async connectedCallback() {
        this.header = this.getSpringCsrfHeaderFromMetaTag(document);
        this.token = this.getSpringCsrfTokenFromMetaTag(document);
        this.cookies = Cookies.get();
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
        this.importers = await ImportEndpoint.getImporterNames();
    }

    extractContentFromMetaTag(element: any) {
        if (element) {
            const value = element.content;
            if (value && value.toLowerCase() !== 'undefined') {
                return value;
            }
        }
        return undefined;
    }

    getSpringCsrfHeaderFromMetaTag(doc: Document) {
        const csrfHeader = doc.head.querySelector('meta[name="_csrf_header"]');
        return this.extractContentFromMetaTag(csrfHeader);
    }

    getSpringCsrfTokenFromMetaTag(doc: Document) {
        const csrfToken = doc.head.querySelector('meta[name="_csrf"]');
        return this.extractContentFromMetaTag(csrfToken);
    }

    getCsrfHeader(doc: Document): any {
        let header: any = {};
        header[this.getSpringCsrfHeaderFromMetaTag(doc)] = this.getSpringCsrfTokenFromMetaTag(doc);
        return header;
    }
}