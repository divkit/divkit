## Render DivJson inside React app

Install the packages first:

```
npm сi
```

Please, note that `@divkit/divkit/dist/client.css` imported as module. The css file can be included in any way.

Then you can use the component as follows:

```tsx
import { DivKit } from '@divkit/react';
import '@divkitframework/divkit/dist/client.css';

<DivKit id="test" json={json} />;
```

This component supports SSR, but contains extra code for this. To split unused code, you need to set env variable `process.env.IS_SERVER` in the build configuration:

```js
new webpack.DefinePlugin({
    'process.env.IS_SERVER': JSON.stringify(isServer),
})
```
