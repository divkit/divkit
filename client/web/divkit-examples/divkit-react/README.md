## Рисуем divjson с реактом

Для работы нужно подключить css: `@yandex-int/divkit/dist/client.css` любым удобным способом.

Для js стоит установить отдельный пакет `@yandex-int/divkit-react`. Он ссылается через peer-зависимость на `@yandex-int/divkit`, поэтому оба пакета нужна установить самостоятельно.

```tsx
import { Divkit } from '@yandex-int/divkit-react';
import '@yandex-int/divkit/dist/client.css';

<Divkit id="test" json={json} />;
```

Компонент `Divkit` поддерживает SSR.

В любом случае нужно настроить сборку и указать константу `process.env.IS_SERVER`:

```js
new webpack.DefinePlugin({
    'process.env.IS_SERVER': JSON.stringify(isServer),
})
```
