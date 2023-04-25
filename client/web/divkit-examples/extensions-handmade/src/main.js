import { render } from '@divkitframework/divkit/client';
import '@divkitframework/divkit/dist/client.css';

class HoverNotifier {
    mountView(node, context) {
        // context can be used to access variables
        // context.variables.get('name')

        // or log errors
        // context.logError(err)

        node.addEventListener('mouseenter', () => {
            console.log('Hovered!');
        });
    }

    // there is also a similar "unmountView" method
}

render({
    id: 'test',
    target: document.querySelector('#root'),
    extensions: new Map([
        ['hover_notifier', HoverNotifier]
    ]),
    json: {
        "card": {
            "log_id": "snapshot_test_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "container",
                        "items": [{
                            "type": "text",
                            "text": "Hello Hover",
                            "background": [{
                                "type": "solid",
                                "color": "#A0FFA0"
                            }],
                            "width": {
                                "type": "wrap_content"
                            },
                            "paddings": {
                                "top": 12,
                                "left": 12,
                                "right": 12,
                                "bottom": 12
                            },
                            "font_size": 20,
                            "extensions": [
                                {
                                    "id": "hover_notifier"
                                }
                            ]
                        }]
                    }
                }
            ]
        }
    }
});
