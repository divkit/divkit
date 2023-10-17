class OldCustomCard1 extends HTMLElement {
    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });
        shadow.textContent = 'hi, i\'m old card';
    }
}
class OldCustomCard2 extends HTMLElement {
    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });
        shadow.textContent = 'and i\'m old as well';
    }
}
class NewCustomCard extends HTMLElement {
    private _counter: number;
    private _timeout: number;
    private _text: HTMLElement;

    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });

        const style = document.createElement('style');

        style.textContent = `
            .component {
                padding: 30px;
                font-size: 20px;
                background: linear-gradient(85deg, #000400, #068200, #1eea00, #FF0000, #fcfa1e, #ccd49b, #7af719);
            }
        `;

        const div = this._text = document.createElement('div');
        div.className = 'component';
        div.textContent = '00:00';

        shadow.appendChild(style);
        shadow.appendChild(div);

        this._counter = 0;
        this._timeout = 0;
        this._tick();
    }

    _tick() {
        this._timeout = window.setTimeout(() => {
            ++this._counter;

            const minutes = Math.floor(this._counter / 60);
            const seconds = this._counter % 60;
            this._text.textContent = String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');

            this._tick();
        }, 1000);
    }

    disconnectedCallback() {
        if (this._timeout) {
            clearTimeout(this._timeout);
            this._timeout = 0;
        }
    }
}
class NewCustomContainer extends HTMLElement {
    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });
        shadow.innerHTML = '<slot></slot>';
    }
}

customElements.define('old-custom-card1', OldCustomCard1);
customElements.define('old-custom-card2', OldCustomCard2);
customElements.define('new-custom-card', NewCustomCard);
customElements.define('new-custom-container', NewCustomContainer);
