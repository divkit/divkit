## Рисуем divjson на клиенте без сборки с ES-модулями

Для работы нужно подключить 2 файла:

* `@yandex-int/divkit/dist/client.css`
* `@yandex-int/divkit/dist/esm/client.mjs`

После этого можно будет рисовать с помощью функции `render`:

```js
import {render} from './node_modules/@yandex-int/divkit/dist/esm/client.mjs';

render({
    id: 'test',
    target: element,
    json: {}
});
```
