## Render DivJson inside browser without build using ES modules

Install the packages first:

```
npm сi
```

To work, these 2 files are included in the page:

* `@divkitframework/divkit/dist/client.css`
* `@divkitframework/divkit/dist/esm/client.mjs`

Using these files, you can render DivJson as follows:

```js
import {render} from './node_modules/@divkitframework/divkit/dist/esm/client.mjs';

render({
    id: 'test',
    target: element,
    json: {}
});
```
