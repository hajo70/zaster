import {customElement} from "lit/decorators.js";
import {View} from "Frontend/views/view.ts";
import {html} from "lit";
import {bookingsViewStore} from "Frontend/views/bookings/bookings-view-store.ts";

@customElement('bookings-view')
export class BookingsView extends View {

    protected override render() {
        return html`
            <div>
                <vaadin-grid
                        .itemHasChildrenPath="${'hasChildren'}"
                        .dataProvider="${bookingsViewStore.boundDataProvider}"
                        .selectedItems=${[bookingsViewStore.selectedAccount]}
                        @active-item-changed=${this.handleGridSelection}
                >
                    <vaadin-grid-tree-column path="accountName"></vaadin-grid-tree-column>
                </vaadin-grid>
            </div>
        `;
    }

    // vaadin-grid fires a null-event when initialized. Ignore it.
    firstSelectionEvent = true;

    handleGridSelection(ev: CustomEvent) {
        if (this.firstSelectionEvent) {
            this.firstSelectionEvent = false;
            return;
        }
        bookingsViewStore.setSelectedAccount(ev.detail.value);
    }
}
