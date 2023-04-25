## Using built-in extensions (and extensions in general)

[General notes about extensions](../../divkit/README.md#extensions)

Install the packages first:

```
npm ci
```

Then build:

```
npm run build
```

And then open `dist/index.html` file to see the result.

### How to use extensions in the json?

Extensions can be attached to all components, using the `extensions` field:

```json
{
    "type": "text",
    "extensions": [
        {
            "id": "size_provider",
            "params": {
                "width_variable_name": "block_width"
            }
        }
    ]
}
```

The `id` field describes which extension should be used, and the `params` property provides configuration parameters for this extension.

### How do I tell DivKit to use different extensions?

The `render` method accepts extensions map, for example:

```js
const map = new Map();

map.set('size_provider', SizeProvider);

render({
    id: 'test',
    target: element,
    json: {},
    extensions: map
});
```

This means that you can provide different implementations for an extension with the same name in different situations.

Another important note, that DivKit doesn't enable any extensions by default.

### SizeProvider extension

The Extension can be imported from the main package:

```js
import { SizeProvider } from '@divkitframework/divkit/client';
```

`SizeProvider` has two configuration parameters: `width_variable_name` and `height_variable_name`. Each time a component is updated, the extension sets the size values for the related variables.

THIS EXTENSION HAS A VERY BAD EFFECT ON THE PERFORMANCE OF YOUR SITE. Any us will result in additional re-layouts in the browser. Anyway, then you can make some layout without `SizeProvider`, do not use it.

### Lottie extension

DivKit doesn't contain full standalone Lottie extension, only a builder for it. This means that DivKit doesn't depend on any Lottie player and doesn't contain it. If you don't need it, you won't download it.

If you need a Lottie extension, you need to install an additional package for it. DivKit contains a builder that is designed to interact with the `lottie-web` package, but you can create your own extension for any other version if you want.

`lottie-web` contains several versions of the player:

* `lottie-web/build/player/lottie` Most compatible player (uses eval!)
* `lottie-web/build/player/lottie_light` Slightly less compatible and smaller player (also without eval)

Use any player of your choise that suits your needs:

```js
import { lottieExtensionBuilder } from '@divkitframework/divkit/client';
import Lottie from 'lottie-web/build/player/lottie';

map.set('lottie', lottieExtensionBuilder(Lottie.loadAnimation));
```
