import { writable } from 'svelte/store';

export interface PagerData {
    instId: string;
    size: number;
    currentItem: number;
    scrollToPagerItem(index: number): void;
}

export function createPagersStore() {
    return writable(new Map<string, PagerData>());
}
