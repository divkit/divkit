class CustomContainer extends HTMLElement {
    constructor() {
        super();

        if (!this.shadowRoot) {
            const shadow = this.attachShadow({ mode: 'open' });
            shadow.innerHTML = '<span class="greeting">Hello unknown</span><slot></slot>';
        }

        this.greeting = this.shadowRoot.querySelector('.greeting');
    }

    divKitApiCallback({ /* logError, */ variables }) {
        const nameVaraible = variables.get('name');
        if (nameVaraible) {
            this.unsubscribeVariable = nameVaraible.subscribe(name => {
                this.greeting.textContent = `Hello ${name}`;
            });
        }
    }

    disconnectedCallback() {
        this.unsubscribeVariable?.();
        this.unsubscribeVariable = null;
    }
}
customElements.define('custom-container', CustomContainer);
