const path = require('path');
const fs = require('fs');
const express = require('express');
const { render } = require('@divkitframework/divkit/server');

const PORT = 3123;

const template = fs.readFileSync(path.resolve(__dirname, 'template.html'), 'utf-8');

function jsonFilter(str) {
    return String(str).replace(/[<>&\u2028\u2029]/g, function(match) {
        switch (match) {
            case '<':
                return '\\u003c';
            case '>':
                return '\\u003e';
            case '&':
                return '\\u0026';
            case '\u2028':
                return '\\u2028';
            case '\u2029':
                return '\\u2029';
        }
        return '';
    });
}

const app = express();

const json = {
    "templates": {
        "wrap_content_text": {
            "type": "text",
            "width": {"type": "wrap_content"}
        }
    },
    "card": {
        "log_id": "snapshot_test_card",
        "variables": [{
            "name": "count",
            "type": "integer",
            "value": 0
        }],
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "wrap_content_text",
                    "text": "Hello divkit @{count}",
                    "action": {
                        "log_id": "text",
                        "url": "div-action://set_variable?name=count&value=@{count + 1}"
                    }
                }
            }
        ]
    }
};

app.get('/', (req, res, _next) => {
    const html = render({
        id: 'test',
        json
    });

    res.send(
        template
            .replace('[% html %]', () => html)
            .replace('[% json %]', () => jsonFilter(JSON.stringify(json)))
    );
});

app.use(express.static(__dirname));

app.listen(PORT, () => {
    console.log(`Server started on http://localhost:${PORT}/`);
});
