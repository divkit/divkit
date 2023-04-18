import Root from './components/Root.svelte';
import { SizeProvider } from './extensions/sizeProvider';
import { lottieExtensionBuilder } from './extensions/lottie';
import type { DivExtensionClass } from '../typings/common';
import Lottie from 'lottie-web/build/player/lottie';

const json = {
    "templates": {},
    "card": {
        "type": "div2",
        "log_id": "snapshot_test_card",
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "text",
                    "text": "Hello world"
                }
            }
        ]
    }
};

window.root = new Root({
    target: document.body,
    props: {
        id: 'abcde',
        json,
        onStat(arg) {
            console.log(arg);
        },
        extensions: new Map<string, DivExtensionClass>([
            ['size_provider', SizeProvider],
            ['lottie', lottieExtensionBuilder(Lottie.loadAnimation)],
        ])
    }
});
