import React from 'react';
import ReactDOMServer from 'react-dom/server';
import { DivKit } from '@divkitframework/react';

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

console.log(ReactDOMServer.renderToString(<DivKit id="test" json={json} />));
