class CustomCard extends HTMLElement {
    constructor() {
        super();

        const shadow = this.attachShadow({ mode: 'open' });

        const style = document.createElement('style');

        style.textContent = `
            .component {
                padding: 30px;
                font-size: 20px;
                background: linear-gradient(85deg, #fff, #068200, #1eea00, #FF0000, #fcfa1e, #ccd49b, #7af719);
            }
        `;

        const div = this._text = document.createElement('div');
        div.className = 'component';
        div.textContent = '00:00';

        shadow.appendChild(style);
        shadow.appendChild(div);
    }

    _updateText() {
        const minutes = Math.floor(this._counter / 60);
        const seconds = this._counter % 60;
        this._text.textContent = String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
    }

    _tick() {
        this._timeout = window.setTimeout(() => {
            ++this._counter;

            this._updateText();

            this._tick();
        }, 1000);
    }

    connectedCallback() {
        const start = this.getAttribute('start');
        this._counter = Number(start) || 0;
        this._timeout = 0;
        this._updateText();
        this._tick();
    }

    disconnectedCallback() {
        if (this._timeout) {
            clearTimeout(this._timeout);
            this._timeout = 0;
        }
    }
}
customElements.define('custom-card', CustomCard);
