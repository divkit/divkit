import type { Shortcut } from './shortcut';

export type Handler = (event: KeyboardEvent) => false | void;

const globalShortcuts = new Set<{
    shortcut: Shortcut;
    handler: Handler;
    prio?: number;
}>();

export let isKeyboard = false;

export function onGlobalKeydown(event: KeyboardEvent) {
    if (event.defaultPrevented) {
        return;
    }

    const matched: {
        handler: Handler;
        prio?: number;
    }[] = [];

    for (const { shortcut, handler, prio } of globalShortcuts) {
        if (shortcut.isPressed(event)) {
            matched.push({ handler, prio });
        }
    }

    matched.sort((a, b) => {
        if (!a.prio) {
            return 1;
        } else if (!b.prio) {
            return -1;
        }
        return b.prio - a.prio;
    });

    if (matched.length) {
        isKeyboard = true;
        setTimeout(() => {
            isKeyboard = false;
        });
    }

    for (let i = 0; i < matched.length; ++i) {
        const { handler } = matched[i];

        if (handler(event) !== false) {
            event.preventDefault();
            break;
        }
    }
}

export function globalHandler(shortcut: Shortcut, handler: Handler, prio?: number) {
    const pair = { shortcut, handler, prio };
    globalShortcuts.add(pair);

    return {
        unbind: () => {
            globalShortcuts.delete(pair);
        }
    };
}
