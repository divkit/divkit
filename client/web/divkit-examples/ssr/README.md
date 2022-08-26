## Render DivJson on the server (SSR) and then hydrate it on the client

Install the packages first:

```
npm ci
```

On the server side, use the `@divkitframework/divkit/server` module:

```js
const html = render({
    id: 'test',
    json
});
```

Then respond with this html.

To hydrate with html on the client, use the similar way as in the other samples with 2 changes:

* Use `client-hydratable` module instead of `client`
* Additionally pass `hydrate: true` option

The `json` should be the same between the client and the server.

```js
Ya.Divkit.render({
    id: 'test',
    target: element,
    hydrate: true,
    json
});
```
