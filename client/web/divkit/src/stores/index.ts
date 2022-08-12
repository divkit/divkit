import type { Writable } from 'svelte/types/runtime/store';
import { createPagersStore } from './pagers';

export const storesMap: Record<string, () => Writable<any>> = {
    pagers: createPagersStore
};
