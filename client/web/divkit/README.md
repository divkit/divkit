## DivKit for Web

### Использование

```
npm i @yandex-int/divkit --save-dev
```

[Примеры использования](../divkit-examples)

[Основная дока про divjson](https://doc.yandex-team.ru/divkit/overview/concepts/about.html)

С любым вариантом использования нужно подключить css файл `dist/client.css` каким-либо способом (сборка, напрямую в хтмл, ...).

Подключить js можно несколькими способами. Нужно определиться с несколькими вопросами:

### Есть ли SSR рендеринг или всё целиком рисуется на клиенте?

#### SSR + гидрация

Тогда стоит использовать часть `/server` на сервере:

```js
import {render} from '@yandex-int/divkit/server';

const html = render({
    id: 'smth',
    json: {
        card: {},
        templates: {}
    },
    onError(details) {
        console.error(details.error);
    }
});
```

И клиентскую с поддержкой гидрации `/client-hydratable` на клиенте:

```js
import {render} from '@yandex-int/divkit/client-hydratable';

render({
    id: 'smth',
    target: document.querySelector('#root'),
    hydrate: true,
    json: {
        card: {},
        templates: {}
    },
    onError(details) {
        console.error(details.error);
    }
});
```

#### Только клиентская отрисовка

Для чисто клиентской отрисовки есть отдельная сборка `/client`. Она чуть меньше по размеру, чем `/client-hydratable`

```js
import {render} from '@yandex-int/divkit/client';

render({
    id: 'smth',
    target: document.querySelector('#root'),
    json: {
        card: {},
        templates: {}
    },
    onError(details) {
        console.error(details.error);
    }
});
```

### Нужны ES-модули или common-js?

Есть сборки и для того, и для того

Node.js должна подхватить нужную версию автоматически

Webpack для сборки должен подхватить версию с ES-модулями

Если вам нужна именно common-js версия, то нужные файлики лежат тут:

```
dist/client.js
dist/client-hydratable.js
dist/server.js
```

Версия с ES-модулями лежат по путям:

```
dist/esm/client.mjs
dist/esm/client-hydratable.mjs
dist/esm/server.mjs
```

При желании, ES-версию можно использовать без сборки в браузере напрямую:

```html
<script type="module">
    import {render} from './node_modules/@yandex-int/divkit/dist/esm/client.mjs';

    render({
        id: 'smth',
        target: document.querySelector('#root'),
        json: {
            card: {},
            templates: {}
        },
        onError(details) {
            console.error(details.error);
        }
    });
</script>
```

### Использование в браузере через window без сборки

```html
<script src="./node_modules/@yandex-int/divkit/dist/browser/client.js"></script>
<script>
    window.Ya.Divkit.render({
        id: 'smth',
        target: document.querySelector('#root'),
        json: {
            card: {},
            templates: {}
        },
        onError(details) {
            console.error(details.error);
        }
    });
</script>
```

### Тайпскрипт и тайпинги

Для всех 3 файлов есть тайпинги, которые должны подхватиться автоматически

### Поддержка браузеров и node.js

Должно работать, но сложные варианты вёрстки могут вести себя по-разному в старых версиях и в новых. Если встретитесь с таким, приносите, пожалуйста
```
chrome >= 58
safari >= 11
firefox >= 67
```

Нода
```
Node.js >= 8
```

### API

Все 3 экспортируемых файла содержат одну функцию: `render`. Функция `render` работает похожим образом, но отличаются входные параметры.

`/client` и `/client-hydratable` принимает `target` - HTML-элемент, в который нужно нарисовать вёрстку.

Версия из `/server` вместо этого возвращает строку с html.

### Параметры

#### id

Строка, обязательный параметр.

Задаёт уникальный идентификатор блока. Он нужен для генерации уникальных id и классов. Соответственно, не должно быть такого, что два блока нарисованы на одной странице с одним значением `id`.

#### json

Обязательный параметр, сам divjson.

#### target

`/client` и `/client-hydratable`

HTML-элемент, в который нужно нарисовать divjson.

Обязательный для клиендсайда и не используется в серверсайде

#### hydrate

`/client-hydratable`

Стоит прислать со значением `true`, если гидрируется вёрстка с SSR.
Также всё ещё можно рисовать элементы чисто на клиенте, не передавая `hydrate`.

Не обязательный

#### onError

Все 3 варианта принимают обязательный параметр `id`. Он нужен для генерации уникальных id и классов. Соответственно, не должно быть такого, что два блока нарисованы на одной странице с одним значением `id`.

#### onStat

`/client` и `/client-hydratable`

Не обязательный колбек, который срабатывает, если пользователь нажал по элементу с `action` или если элемент имеет `visibility_action` и сработало условие:

```js
function onStat(details) {
    // details.type: 'click' | 'visible'
    // details.action: action | visibility_action
}
```

Также все 3 варианта позволяют задать обработчик ошибок `onError`:

```js
function onError(details) {
    console.error(details.error);
}
```

`details.error` имеет тип Error с дополнительными полями `level` и `additional`. Формат задуман для совместимости с [error-counter](https://github.yandex-team.ru/RUM/error-counter):

```js
function onError(details) {
    Ya.Rum.logError({}, details.error);
}
```

#### platform

`desktop` | `touch` | `auto`

По умолчанию `auto` - пытается определить платформу по тачевым событиям. Влияет на ховеры, стрелочки в галерее.


#### theme (ЭКСПЕРИМЕНТАЛЬНОЕ)

`system` | `light` | `dark`

По умолчанию `system`. Влияет на работу переменных из палитры.


### Поддержка палитры (ЭКСПЕРИМЕНТАЛЬНОЕ)

В `json` рядом с `card` и `templates` можно передать поле `palette` с цветами для светлой и тёмной темы:

```json
{
    "card": {
        "states": [
            {
                "div": {
                    "type": "text",
                    "text": "Hello palette",
                    "text_color": "@{text}",
                    "background": [{
                        "type": "solid",
                        "color": "@{bg}"
                    }]
                },
                "state_id": 0
            }
        ],
        "log_id": "test"
    },
    "templates": {},
    "palette": {
        "dark": [
            {
                "name": "bg",
                "color": "#000"
            },
            {
                "name": "text",
                "color": "#fff"
            }
        ],
        "light": [
            {
                "name": "bg",
                "color": "#fff"
            },
            {
                "name": "text",
                "color": "#000"
            }
        ]
    }
}
```
