import { writable } from 'svelte/store';

export type EditorMode = 'json' | null;

export const editorMode = writable<EditorMode>('json');
