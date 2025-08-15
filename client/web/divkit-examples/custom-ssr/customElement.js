class CustomContainer extends HTMLElement {
    constructor() {
        super();

        if (!this.shadowRoot) {
            const shadow = this.attachShadow({ mode: 'open' });
            shadow.innerHTML = 'Hello custom container<slot></slot>';
        }
    }
}
customElements.define('custom-container', CustomContainer);
