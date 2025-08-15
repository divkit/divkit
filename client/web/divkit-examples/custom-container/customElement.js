class CustomContainer extends HTMLElement {
    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });
        shadow.innerHTML = '<slot></slot>';
    }
}
customElements.define('custom-container', CustomContainer);
