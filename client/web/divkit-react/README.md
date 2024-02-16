## DivKit for the Web (React way)

React component wrapper to render DivJson, using the `@divkitframework/divkit` module.

[Main doc](../divkit/README.md).

### Installation

```
npm i @divkitframework/react --save
```

### Usage

```tsx
import { DivKit } from '@divkitframework/react';

return <DivKit id="smth" json={json} />;
```

All other props are similar to the `render` options for the `@divkitframework/divkit` module.

### SSR

SSR is supported. Make sure to pass the same props both on the client and the server.

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
