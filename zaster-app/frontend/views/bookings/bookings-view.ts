import {customElement} from "lit/decorators.js";
import {View} from "Frontend/views/view.ts";
import {html} from "lit";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

@customElement('bookings-view')
export class BookingsView extends View {

    protected override render() {
        return html`
            <div class="content flex gap-m h-full">
                <vaadin-grid
                        .itemHasChildrenPath="${'hasChildren'}"
                        .dataProvider="${bookingsViewStore.dataProvider}"
                >
                    <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                </vaadin-grid>
            </div>
        `;
    }
}
