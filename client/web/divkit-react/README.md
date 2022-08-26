## DivKit for the Web (React way)

React component wrapper to render DivJson, using the `@divkitframework/divkit` module.

[Main doc](../divkit/README.md).

### Usage

```tsx
import { DivKit } from '@divkitframework/react';

return <DivKit id="smth" json={json} />;
```

All other props are similar to the `render` options for the `@divkitframework/divkit` module.

### Important: Configure your build config to minimize the result code size

For this, specify the `process.env.IS_SERVER` env variable:

```js
new webpack.DefinePlugin({
    'process.env.IS_SERVER': JSON.stringify(isServer)
})
```

Where `isServer` - current env, `true` or `false`.

### SSR

SSR is supported. Make sure to pass the same props both on the client and the server.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
