import { render } from '@divkitframework/divkit/client';
import '@divkitframework/divkit/dist/client.css';

render({
    id: 'test',
    target: document.querySelector('#root'),
    json: {
        "templates": {
            "wrap_content_text": {
                "type": "text",
                "width": {"type": "wrap_content"}
            }
        },
        "card": {
            "log_id": "snapshot_test_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "wrap_content_text",
                        "text": "Hello divkit"
                    }
                }
            ]
        }
    }
});
