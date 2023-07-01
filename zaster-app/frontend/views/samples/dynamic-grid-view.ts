import {View} from "Frontend/views/view.ts";
import {customElement, state} from "lit/decorators.js";
import {html} from "lit";
import {repeat} from 'lit/directives/repeat.js';

import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/text-field';
import '@vaadin/icon';

import {columnBodyRenderer, GridColumnBodyLitRenderer} from "@vaadin/grid/lit";
import AccountGroupDto from "Frontend/generated/de/spricom/zaster/dtos/tracking/AccountGroupDto.ts";
import ColumnGroupDto from "Frontend/generated/de/spricom/zaster/dtos/samples/ColumnGroupDto.ts";
import {SamplesEndpoint} from "Frontend/generated/endpoints.ts";
import ColumnGroupDtoModel from "Frontend/generated/de/spricom/zaster/dtos/samples/ColumnGroupDtoModel.ts";
import {Binder, field} from "@hilla/form";

@customElement("dynamic-grid-view")
export class DynamicGridView extends View {

    @state()
    private columns: ColumnGroupDto = ColumnGroupDtoModel.createEmptyValue();

    @state()
    private data: number[] = [];

    private binder = new Binder(this, ColumnGroupDtoModel);

    protected override render() {
        return html`
            <div class="content flex gap-m h-full">
                <vaadin-grid class="grid h-full"
                             .items=${this.data}
                >
                    ${repeat(this.binder.model.columns, (columnBinder) =>
                            html`
                                <vaadin-grid-column header=${columnBinder.value?.name}
                                                    auto-width
                                                    ${columnBodyRenderer(this.cellRenderer(() => columnBinder.value?.name || ''), [])}
                                ></vaadin-grid-column>
                            `)}
                </vaadin-grid>
                <div>
                    <h2>Spalten:</h2>
                    ${repeat(this.binder.model.columns, (columnBinder, index) =>
                            html`
                                <div>
                                    <span>${index}.</span>
                                    <vaadin-text-field
                                            ${field(columnBinder.model.name)}
                                    ></vaadin-text-field>
                                    <vaadin-icon icon="lumo:cross"
                                                 @click=${() => columnBinder.removeSelf()}
                                    ></vaadin-icon>
                                </div>
                            `)
                    }
                    <vaadin-button
                            @click=${() => this.binder.for(this.binder.model.columns).appendItem()}
                    >
                        <vaadin-icon icon="lumo:plus"></vaadin-icon>
                    </vaadin-button>
                    <vaadin-button
                            @click=${this.updateGrid}
                    >
                        <vaadin-icon icon="lumo:download"></vaadin-icon>
                    </vaadin-button>
                </div>
            </div>
        `;
    }

    private cellRenderer(columnName: () => string): GridColumnBodyLitRenderer<AccountGroupDto> {
        return (row, model) => html`
            <span>${row}/${model.index} (${columnName()})</span>
        `;
    }

    private updateGrid() {
        this.binder.submitTo(this.updateColumns);
        this.data = [...this.data, this.data.length];
    }

    async updateColumns(columnGroup: ColumnGroupDto) {
        this.columns = columnGroup;
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
        for (let i = 1; i < 100; i++) {
            this.data.push(i);
        }
        this.columns = await SamplesEndpoint.getColumnGroup();
        this.binder.read(this.columns);
    }
}