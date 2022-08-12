import type { Readable } from 'svelte/types/runtime/store';

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
