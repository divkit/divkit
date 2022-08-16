import { writable } from 'svelte/store';
import { DEFAULT_JSON_VALUE } from './initialValue';

export const valueStore = writable(DEFAULT_JSON_VALUE);
export const initialValueStore = writable(DEFAULT_JSON_VALUE);
export const codeRunStore = writable(false);
