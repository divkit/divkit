## Using custom components

[General notes about custom components](../../divkit/README.md#customComponents)

Install the packages first:

```
npm ci
```

And then open `index.html` file with any http server to see the result (the browser may block loading  of external modules into the file:// pages).

### What's the idea?

In json markup you can provide a special component:

```json
// DivJson
{
    "type": "custom",
    "custom_type": "custom_card"
}
```

Without any help, DivKit will not know about this special component. This can be done using the `customComponents` map:

```js
// JS code
render({
    id: 'test',
    target: document.querySelector('#root'),
    customComponents: new Map([
        ['custom_card', {
            element: 'custom-card'
        }]
    ]),
    json: {
        ...
    }
});
```

Now we need to create our `custom-card` component. This can be done using the power of Custom Elements:

```js
// JS Code
class CustomCard extends HTMLElement {
    constructor() {
        super();

        // some logic
    }
}
customElements.define('custom-card', CustomCard);
```

This code defines a custom element inside the browser. So, any `custom` component inside DivKit should be a Custom Element in terms of HTML.

### How can I provide additional data for a component?

`custom` components in DivKit can accept an additional field `custom_props`. This is an object, and all its properties are directly mapped as attributes of your Custom Element:

```js
    // DivJson
    "custom_props": {
        "start": 15
    }

    // ->

    // HTML
    <custom-card start="15"></custom-card>
```

Keep in mind that there are no integer attributes here, all additional properties will be rendered as regular html attributes.

Next, your Custom Element should read these properties:

```js
    // JS code
    connectedCallback() {
        const start = this.getAttribute('start');
    }
```

Keep in mind that these additional attributes will only be available in the `connectedCallback`, and not in the `constructor` of the Custom Element.

### What about browser support?

Custom elements have been supported in all modern browsers for a while. There are some nuances that will be covered in the following examples, but basic support for Custom Elements v1 will be a good start for your components.

### How can I wrap other DivKit components with my Custom Component?

See [next example](../custom-container).
