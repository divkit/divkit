import React from 'react';
import { hydrateRoot } from 'react-dom/client';
import { DivKit } from '@divkitframework/react';
import '@divkitframework/divkit/dist/client.css';

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

const container = document.querySelector('#root');
hydrateRoot(container, <DivKit id="test" json={json} />);
