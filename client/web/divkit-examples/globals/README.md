## Render DivJson inside browser without build

Install the packages first:

```
npm сi
```

To work, these 2 files are included in the page:

* `@divkitframework/divkit/dist/client.css`
* `@divkitframework/divkit/dist/browser/client.js`

Using these files, you can render DivJson as follows:

```js
Ya.Divkit.render({
    id: 'test',
    target: element,
    json: {}
});
```
