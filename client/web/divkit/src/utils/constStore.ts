import type { Readable } from 'svelte/store';

// eslint-disable-next-line @typescript-eslint/no-empty-function
function voidUnsubscribe() {
}

export function constStore<T>(val: T): Readable<T> {
    return {
        subscribe(cb) {
            cb(val);
            return voidUnsubscribe;
        }
    };
}

export const constUndefStore = constStore(undefined);
