import type { Writable } from 'svelte/store';
import { createPagersStore } from './pagers';

export const storesMap: Record<string, () => Writable<any>> = {
    pagers: createPagersStore
};
