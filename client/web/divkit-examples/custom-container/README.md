## Using DivKit components inside your Custom Component

[General notes about custom components](../../divkit/README.md#customComponents)

[Previous example](../custom-simple)

Install the packages first:

```
npm ci
```

And then open `index.html` file with any http server to see the result (the browser may block loading  of external modules into the file:// pages).

### How can support a DivKitComponents inside my Custom Component?

First of all, lets look at the markup:

```js
    // DivJson
    "type": "custom",
    "custom_type": "custom_container",
    "items": [
        ...
    ]
```

These `items` are the DivKit markup and will be rendered by DivKit itself.

Now we will provide a place (`slot`) for these `items`:

```js
    // JS code
    const shadow = this.attachShadow({ mode: 'open' });
    shadow.innerHTML = '<slot></slot>';
```

This `<slot>` can be located at any depth of your element, not just a direct child of ShadowRoot.

### How can I render Custom DivKit components on the Server Side?

See [next example](../custom-ssr).
