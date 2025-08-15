import { writable } from 'svelte/store';

export type EditorMode = 'json' | 'ts' | null;

export const editorMode = writable<EditorMode>('json');
