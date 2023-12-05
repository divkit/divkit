import { writable } from 'svelte/store';

export const session = writable({
    uuid: '',
    writeKey: ''
});

export const isInitialLoading = writable(false);

export const isLoadError = writable(false);

export const isSamples = writable(false);

export const isDesign = writable(false);
