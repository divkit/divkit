## Writing your own extension

[General notes about extensions](../../divkit/README.md#extensions)

[More information about using extensions](../extensions-builtin/README.md)

Install the packages first:

```
npm ci
```

Then build:

```
npm run build
```

And then open `dist/index.html` file to see the result.

### Extension class

All extensions are a class with a constructor and optional methods `mountView` and `unmountView`:

```js
class MyExtension {
    constructor(params) {}

    mountView(node, context) {}

    unmountView(node, context) {}
}
```

Using `mountView` you can access the DOM node. The `unmountView` method is suitable for cleaning purposes if you need it.

### Compatibility

Be sure to test your extenson on any DivKit update, even on a minor and patch versions.
