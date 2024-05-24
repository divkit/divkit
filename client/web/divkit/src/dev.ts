import Root from './components/Root.svelte';
import { SizeProvider } from './extensions/sizeProvider';
import { lottieExtensionBuilder } from './extensions/lottie';
import type { DivExtensionClass } from '../typings/common';
import Lottie from 'lottie-web/build/player/lottie';
import { initComponents } from './devCustomComponents';

const json = {
  "templates": {},
  "card": {
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
        ]),
        customComponents: new Map([
            ['old_custom_card_1', {
                element: 'old-custom-card1'
            }],
            ['old_custom_card_2', {
                element: 'old-custom-card2'
            }],
            ['new_custom_card_1', {
                element: 'new-custom-card'
            }],
            ['new_custom_card_2', {
                element: 'new-custom-card'
            }],
            ['new_custom_container_1', {
                element: 'new-custom-container'
            }]
        ]),
        store: {
            getValue(name, type) {
                try {
                    const json = localStorage.getItem('divkit:' + name);
                    if (json) {
                        const parsed = JSON.parse(json);
                        if (type === parsed.type && Date.now() < parsed.lifetime && parsed.value) {
                            return parsed.value;
                        }
                    }
                } catch (err) {
                    //
                }
            },
            setValue(name, type, value, lifetime) {
                try {
                    localStorage.setItem('divkit:' + name, JSON.stringify({value, type, lifetime: Date.now() + lifetime * 1000}));
                } catch (err) {
                    //
                }
            },
        }
    }
});

initComponents();
