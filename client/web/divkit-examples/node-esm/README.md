## Render DivJson in Node.js with ES modules

Install the package first:

```
npm i @divkitframework/divkit
```

```js
import { render } from '@divkitframework/divkit/server';

const html = render({
    id: 'test',
    json
});
```
