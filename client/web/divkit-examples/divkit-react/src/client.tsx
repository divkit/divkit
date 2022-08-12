import React from 'react';
import ReactDOM from 'react-dom';
import { Divkit } from '@yandex-int/divkit-react';
import '@yandex-int/divkit/dist/client.css';

const json = {
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
};

ReactDOM.hydrate(
    <Divkit id="test" json={json} />,
    document.querySelector('#root')
);
