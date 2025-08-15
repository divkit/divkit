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
        "news_item_template": {
            "type": "text",
            "font_size": 16,
            "line_height": 20,
            "text_color": "#333",
            "$text": "news_item_text",
            "paddings": {
                "left": 12,
                "right": 12,
                "top": 8
            }
        }
    },
    "card": {
        "log_id": "custom_card",
        "variables": [{
            "type": "string",
            "name": "name",
            "value": "user"
        }],
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "container",
                    "margins": {
                        "left": 16,
                        "top": 16,
                        "right": 16,
                        "bottom": 16
                    },
                    "items": [
                        {
                            "type": "input",
                            "text_variable": "name",
                            "font_size": 20,
                            "accessibility": {
                                "description": "Name"
                            }
                        },
                        {
                            "type": "custom",
                            "custom_type": "custom_container",
                            "items": [
                                {
                                    "type": "news_item_template",
                                    "news_item_text": "This is div-text item 1"
                                },
                                {
                                    "type": "news_item_template",
                                    "news_item_text": "This is div-text item 2"
                                },
                                {
                                    "type": "news_item_template",
                                    "news_item_text": "This is div-text item 3"
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    }
};

app.get('/', (req, res, _next) => {
    const html = render({
        id: 'test',
        json,
        customComponents: new Map([
            ['custom_container', {
                element: 'custom-container',
                template({ /* props, */ variables }) {
                    return `<span class="greeting">Hello ${variables.get('name') || 'unknown'}</span><slot></slot>`;
                }
            }]
        ])
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
