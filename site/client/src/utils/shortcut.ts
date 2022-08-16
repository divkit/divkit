import capitalize from './capitalize';
import { isMac } from './isMac';

const MODIFIERS = {
    SHIFT: { val: 4, field: 'shiftKey', str: '⇧' },
    CTRL: { val: 1, field: 'ctrlKey', str: '⌃' },
    ALT: { val: 2, field: 'altKey', str: '⌥' },
    CMD: { val: 8, field: 'metaKey', str: '⌘' }
};

const KEYS = {
    BACKSPACE: 8,
    DELETE: 46,
    LEFT: 37,
    UP: 38,
    RIGHT: 39,
    DOWN: 40,
    ENTER: 13,
    SPACE: 32,
    ESC: 27,
    TAB: 9,
    HOME: 36,
    END: 35,
    PAGEUP: 33,
    PAGEDOWN: 34,
    A: 65,
    C: 67,
    D: 68,
    E: 69,
    F: 70,
    H: 72,
    I: 73,
    J: 74,
    K: 75,
    L: 76,
    M: 77,
    N: 78,
    P: 80,
    S: 83,
    T: 84,
    U: 85,
    V: 86,
    X: 88,
    Y: 89,
    Z: 90,
    '1': 49,
    '2': 50,
    '3': 51,
    '/': 191
};

const KEYS_INVERT: Record<number, string> = Object.keys(KEYS).reduce((acc: Record<number, string>, item: string) => {
    acc[KEYS[item as keyof typeof KEYS]] = item;
    return acc;
}, {});

export default class Shortcut {
    static MAC_SPLITTER = ':';
    static SEVERAL_SPLITTER = ' / ';

    private readonly _parts: {
        which: number;
        modifiers: number;
    }[];

    private readonly _strParts: string[][];

    private readonly _str: string;

    constructor(shortcut: string) {
        if (shortcut.includes(Shortcut.MAC_SPLITTER)) {
            shortcut = shortcut.split(Shortcut.MAC_SPLITTER)[isMac ? 1 : 0].trim();
        }

        this._parts = [];
        this._strParts = [];
        for (const part of shortcut.split(Shortcut.SEVERAL_SPLITTER)) {
            const trimmed = part.trim();
            this._parts.push(this._parse(trimmed));
            this._strParts.push(trimmed.split('+'));
        }

        if (isMac) {
            this._str = this._genMacStr();
        } else {
            this._str = shortcut;
        }
    }

    _parse(shortcut: string) {
        const splitted = shortcut.split('+');
        let modifiers = 0;
        let which;

        for (const keyOrModifier of splitted) {
            const upper = keyOrModifier.toUpperCase();

            if (!(upper in MODIFIERS) && !(upper in KEYS)) {
                throw new Error(`Unknown key ${shortcut}`);
            }
            if (upper in MODIFIERS) {
                modifiers += MODIFIERS[upper as keyof typeof MODIFIERS].val;
            } else {
                which = KEYS[upper as keyof typeof KEYS];
            }
        }

        if (!which) {
            throw new Error(`Incorrect shortcut ${shortcut}`);
        }

        return { which, modifiers };
    }

    _genMacStr() {
        return this._parts.map(part => {
            let res = '';

            const modifiers = part.modifiers;
            for (const key in MODIFIERS) {
                // eslint-disable-next-line no-bitwise
                if (modifiers & MODIFIERS[key as keyof typeof MODIFIERS].val) {
                    res += MODIFIERS[key as keyof typeof MODIFIERS].str;
                }
            }

            res += capitalize(KEYS_INVERT[part.which].toLowerCase());

            return res;
        }).join(Shortcut.SEVERAL_SPLITTER);
    }

    toString() {
        return this._str;
    }

    isPressed(event: KeyboardEvent) {
        for (const part of this._parts) {
            if (event.which !== part.which) {
                continue;
            }

            let modifiers = 0;

            for (const key in MODIFIERS) {
                if (event[MODIFIERS[key as keyof typeof MODIFIERS].field as 'shiftKey' | 'ctrlKey' | 'altKey' | 'metaKey']) {
                    modifiers += MODIFIERS[key as keyof typeof MODIFIERS].val;
                }
            }

            if (modifiers !== part.modifiers) {
                continue;
            }

            return true;
        }

        return false;
    }

    getList() {
        return this._strParts;
    }
}
