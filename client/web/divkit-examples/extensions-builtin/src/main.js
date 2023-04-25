import { render, SizeProvider, lottieExtensionBuilder } from '@divkitframework/divkit/client';
import '@divkitframework/divkit/dist/client.css';
import Lottie from 'lottie-web/build/player/lottie';

render({
    id: 'test',
    target: document.querySelector('#root'),
    extensions: new Map([
        ['size_provider', SizeProvider],
        ['lottie', lottieExtensionBuilder(Lottie.loadAnimation)],
    ]),
    json: {
        "card": {
            "log_id": "snapshot_test_card",
            "variables": [
                {
                    "name": "block_width",
                    "type": "integer",
                    "value": 0
                }
            ],
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "container",
                        "items": [{
                            "type": "text",
                            "text": "Hello Extensions! Both blocks have the same width",
                            "background": [{
                                "type": "solid",
                                "color": "#A0FFA0"
                            }],
                            "width": {
                                "type": "wrap_content"
                            },
                            "margins": {
                                "bottom": 12
                            },
                            "font_size": 20,
                            "extensions": [
                                {
                                    "id": "size_provider",
                                    "params": {
                                        "width_variable_name": "block_width"
                                    }
                                }
                            ]
                        }, {
                            "type": "gif",
                            "extensions": [
                                {
                                    "id": "lottie",
                                    "params": {
                                        "lottie_url": "https://yastatic.net/s3/home/yandex-app/div_test_data/love_anim.json"
                                    }
                                }
                            ],
                            "gif_url": "empty",
                            "width": {
                                "type": "fixed",
                                "value": "@{block_width}"
                            },
                            "aspect": {
                                "ratio": 1
                            },
                            "background": [{
                                "type": "solid",
                                "color": "#A0FFA0"
                            }]
                        }]
                    }
                }
            ]
        }
    }
});
