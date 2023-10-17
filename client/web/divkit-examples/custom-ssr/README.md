## Using DivKit components inside your Custom Component

[General notes about custom components](../../divkit/README.md#customComponents)

[Previous example with a simple component](../custom-simple)

[Previous example with container component](../custom-container)

[General info about SSR in DivKit](../ssr)

This example assumes that you are familiar with the ideas from the previous examples.

Install the packages first:

```
npm ci
```

### Run sample

```shell
npm start
```

### How does it work without items?

The server code contains a similar `customComponents` map and will produce something like this:

```html
    // HTML
    <custom-container></custom-container>
```

On the client side, this markup will be hydrated. So, what about markup inside the Custom Element?

Lets assume that our component has some markup, for example, `Hello custom container`:

```html
    // HTML
    <custom-container>
        <!-- rendered on client -->
        Hello custom container
        <!-- rendered on client -->
    </custom-container>
```

Custom elements is defined on the client side, and their markup is created in the browser. This means that if we don't do anything, our glorious SSR (Server-side rendering) will produce incomplete markup, which will be changed after the Custom Element is defined. The component will work as expected, but the user may notice a layout shift and a markup change.

Declarative shadow dom comes to the rescue! If our `customComponents` contain not only the tag name of the Custom Element, but also a template itself, DivKit will produce a `<template>` for this component for us:

```js
    // JS code
    {
        element: 'custom-container',
        template: 'Hello custom container'
    }

    // HTML
    <custom-container>
        <template shadowrootmode="open">
            Hello custom container
        </template>
    </custom-container>
```

This markup will be parsed in the browser directly into the shadow dom:

```html
    <custom-container>
        #shadow-root (open)
        Hello custom container
    </custom-container>
```

In this case, the browser will be able to show the exact page even without running JS on the client side, which means that there will be no layout shift on our page and it will be persisted during the entire page load.

Don't forget to change your custom element code:

```js
    // JS Code
    if (this.shadowRoot) {
        // Declarative shadow dom is supported and parsed by the browser
    } else {
        // Create a shadow root as usual
        const shadow = this.attachShadow({ mode: 'open' });
        // ...
    }
```

### How it works with items?

Literally the same. Don't forget to include `<slot></slot>` in your template, and everything will be fine:

```js
    // JS code
    {
        element: 'custom-container',
        template: 'Hello custom container<slot></slot>'
    }

    // HTML Server response
    <custom-container>
        <template shadowrootmode="open">
            Hello custom container
        </template>

        <div>
            <!--some DivKit markup-->
        </div>
    </custom-container>

    // In the browser parsed HTML
    <custom-container>
        #shadow-root (open)
        Hello custom container
        <slot>
            ↳
            <div>
                <!--some DivKit markup-->
            </div>
        </slot>
    </custom-container>
```

### What about browser support?

Declarative Shadow DOM is currently supported only in Chromium-based browsers and in Safari. Firefox currently does not support this, but work on supporting this [will begin soon](https://github.com/mozilla/standards-positions/issues/335#issuecomment-1714182997).

### What about old browsers that will receive markup using that feature?

This will create a `<template>` element, but this template will not create shadow root automatically, so your component should do it the classic way. In conclusion, your Custom Component will work, but SSR will be "incomplete" in this browser, and the layout will be changed after JS execution.
