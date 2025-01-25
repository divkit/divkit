## DivKit Visual Editor

### Install

```sh
npm i @divkitframework/visual-editor
```

### Render editor

```ts
import '@divkitframework/visual-editor/dist/divkit-editor.css';
import { DivProEditor } from '@divkitframework/visual-editor';

const instance = DivProEditor.init({
    renderTo: document.body,
    value: JSON.stringify({
        "card": {
            "log_id": "test",
            "states": [{
                "state_id": 0,
                "div": {
                    "type": "container",
                    "orientation": "overlap",
                    "items": [{
                        "type": "text",
                        "text": "Hello world"
                    }]
                }
            }]
        }
    })
});

// ...

instance.getValue();
```
