## Рисуем divjson с вебпаком и тайпскриптами

Для работы нужно подключить css: `@yandex-int/divkit/dist/client.css` любым удобным способом.

Для js стоит подключать `@yandex-int/divkit/client`.

После этого можно будет рисовать с помощью функции `render`:

```js
import {render} from '@yandex-int/divkit/client';

render({
    id: 'test',
    target: element,
    json: {}
});
```

Тайпинги должны подцепиться автоматически.
