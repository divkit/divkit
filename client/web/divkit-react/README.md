## DivKit for the Web (React way)

React component wrapper to render DivJson, using the `@divkitframework/divkit` module.

[Main doc](../divkit/README.md).

### Installation

```
npm i @divkitframework/react --save
```

### Usage

```tsx
import { useRef } from 'react';
import { DivKit, type DivKitHandle } from '@divkitframework/react';

const ref = useRef<DivKitHandle>(null);

return <DivKit ref={ref} id="smth" json={json} />;
```

All other props are similar to the `render` options for the `@divkitframework/divkit` module.

### Imperative API

`DivKit` supports `ref` with imperative API:

```tsx
ref.current?.applyPatch(patch);
```

### SSR

SSR is supported. Make sure to pass the same props both on the client and the server.

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).
