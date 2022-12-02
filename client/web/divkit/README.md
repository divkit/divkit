## DivKit for the Web

![client code size](https://img.shields.io/badge/client%20lib%20minified&brotli-47.7%20KB-brightgreen)

### Installation

```
npm i @divkitframework/divkit --save
```

### Usage

[Example usage repos](../divkit-examples)

For all variants of usage, css file `dist/client.css` is required. Include it in any way (import as module, link directly in html, etc).

JS code can be bundled with various strategies. Basically you need to answer a few questions

### Is there any server-side rendering (SSR) or will it be only on client?

#### SSR + hydration

On the server side there is `/server` module:

```js
import {render} from '@divkitframework/divkit/server';

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

Then use `/client-hydratable` on client to hydrate server-side html::

```js
import {render} from '@divkitframework/divkit/client-hydratable';

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

#### Client-only rendering

For the client-only usage there is `/client` module. The size of this module is slightly smaller than `/client-hydratable`.

```js
import {render} from '@divkitframework/divkit/client';

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

### Are you want to use ES module or CommonJS module?

The package contains both of them.

Node.js will automatically use the appropriate version.

Webpack will use ES modules version.

For the direct CommonJS usage, this files can be used:

```
dist/client.js
dist/client-hydratable.js
dist/server.js
```

ES modules files:

```
dist/esm/client.mjs
dist/esm/client-hydratable.mjs
dist/esm/server.mjs
```

ES modules can be used in the browser directly without any build:

```html
<script type="module">
    import {render} from './node_modules/@divkitframework/divkit/dist/esm/client.mjs';

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

### Use in the browser via global variables without build:

```html
<script src="./node_modules/@divkitframework/divkit/dist/browser/client.js"></script>
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

### Usage with React

There is a separate package for that.

[React component package](../divkit-react)

### TypeScript and types

All modules have typescript definitions (client, client-hydratable and server), so typescript will load them at any use.

### Browser/Node.js support

Browser support
```
chrome >= 58
safari >= 11
firefox >= 67
```

Node.js
```
Node.js >= 8
```

### API: render

All 3 exported modules have an exported function `render`. This function works in a similar way on client and server.

`/client` and `/client-hydratable` requires option `target` - an HTML-element that is used to render json.

Instead, `/server` module will return an HTML string.

#### id

String, required.

Means the unique block identifier. Used to generate ids and classes. There should not be 2 blocks with the same `id` on the page.

#### json

Object, required.

Divjson itself.

#### target

`/client` and `/client-hydratable`

HTML-element, required.

#### hydrate

`/client-hydratable`

Boolean, optional.

It must be `true`, if the current render must hydrate html in `target`.

#### onError

Function, optional.

Callback for errors and warnings for manual processing.

```js
function onError({error}) {
    console.log(error.level, error.additional, error.message);
}
```

#### onStat

`/client` and `/client-hydratable`

Function, optional.

Used for logging clicks (for elements with `action`) and visibility logging (for elements with `visibility_action`).

```js
function onStat(details) {
    // details.type: 'click' | 'visible'
    // details.action: action | visibility_action
}
```

#### platform

`desktop` | `touch` | `auto`

The default value is `auto`. Tweaks for mouse or touch events.

#### mix

String, optional.

An additional class added to the root element.


#### theme (EXPERIMENTAL)

`system` | `light` | `dark`

The default value is `system`. Affects variables in `palette`.


### Palette support (EXPERIMENTAL)

Divjson along with the `card` and `templates` can contain a `palette` property with colors for light and dark themes:

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

The theme can be changed at any time:

```js
instance.setTheme('dark');
```

### API: other

#### Variables

```js
import {createVariable, createGlobalVariablesController, render} from '@divkit/divkit';

// Custom variable outside of DivJson
const variable = createVariable('name', 'string', 'val');

variable.subscribe(newVal => {
    console.log(newVal);
});

// Custom scope for variables
const controller = createGlobalVariablesController();

controller.setVariable(variable);

render(({
    id: 'smth',
    target: document.querySelector('#root'),
    json: {},
    globalVariablesController: controller
});
```

---

[Documentation](https://divkit.tech/doc). [Medium tutorial](https://medium.com/p/cad519252f0f). [Habr tutorial](https://habr.com/ru/company/yandex/blog/683886/).

Telegram: [News](https://t.me/divkit_news) | [English-speaking chat](https://t.me/divkit_community_en) | [Чат на русском](https://t.me/divkit_community_ru).

[Twitter](https://twitter.com/DivKitFramework)
