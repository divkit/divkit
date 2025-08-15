## Using DivKit components inside your Custom Component

[General notes about custom components](../../divkit/README.md#customComponents)

[Previous example with a simple component](../custom-simple)

[Previous example with container component](../custom-container)

[Previous example with SSR explained](../custom-ssr)

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

### Intro

In the previous example, we walked through the process of creating Custom DivKit Components both on the server and the client side. The components were very simple, with static markup. But how does this work when our components depend on `variables` or `custom_props` in JSON?

### Client side api

On the client-side, one can provide a `divKitApiCallback` method for the Custom Elements class. If the method is present, DivKit will invoke it after the `constructor` method has been called and after the `connectedCallback` method has completed.

This callback function will be called using this api:

```ts
    divKitApiCallback({
        logError(error: WrappedError): void;
        variables: Map<string, Variable>;
    }): void;
```

`variables` from this api can be used to get an instance of a variable, and you can subscribe to its value:

```ts
    divKitApiCallback({ variables }) {
        const instance = variables.get('name');
        if (instance) {
            instance.subscribe(newValue => console.log);
        }
    }
```

The callback for `subscribe` will be called instantly with the current value.

So, we have created a subscription to our variable, and our Custom Element is in sync with this variable. But what if the component is disconnected from the DOM?

In this case, do not forget to unsubscribe from the variable:

```ts
    divKitApiCallback({ variables }) {
        const instance = variables.get('name');
        if (instance) {
            this.unsubscribeVariable = instance.subscribe(newValue => console.log);
        }
    }

    disconnectedCallback() {
        this.unsubscribeVariable?.();
        this.unsubscribeVariable = null;
    }
```

No more memory leaks!

### Server side api

On the server side, static HTML will be generated as a result, so the api looks different:

```ts
    customComponents: new Map([
        ['custom_container', {
            element: 'custom-container',
            template({ /* props, */ variables }) {
                return `<span class="greeting">Hello ${variables.get('name') || 'unknown'}</span><slot></slot>`;
            }
        }]
    ])
```

`variables` is a Map with static variable values here, not instances!

It is also a `props` object with the value `custom_props` from the json.

Both `variables` and `props` can be used to customize the HTML of your Custom Element.
