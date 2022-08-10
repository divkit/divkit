## Рисуем divjson на сервере с ES-модулями

Для отрисовки на сервере стоит использовать тот же самый модуль `@yandex-int/divkit/server`, что и в примере [ssr](../ssr):

```js
import { render } from '@yandex-int/divkit/server';

const html = render({
    id: 'test',
    json
});
```
