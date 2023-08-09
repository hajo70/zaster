import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {View} from '../../views/view.js';

@customElement('about-view')
export class AboutView extends View {
  render() {
    return html`<div>
      <img style="width: 200px;" src="images/empty-plant.png" />
      <h2 class="mt-xl mb-m">This place intentionally left empty</h2>
      <p>It’s a place where you can grow your own UI 🤗</p>
      <a href="/h2-console/" target="_blank">H2-Console</a>
    </div>`;
  }

  connectedCallback() {
    super.connectedCallback();
    this.classList.add(
      'flex',
      'flex-col',
      'h-full',
      'items-center',
      'justify-center',
      'p-l',
      'text-center',
      'box-border'
    );
  }
}
