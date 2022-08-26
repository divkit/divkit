const { render} = require('@divkitframework/divkit/server');

console.log(render({
    id: 'test',
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
}));
