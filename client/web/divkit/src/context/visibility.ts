import type { Readable } from 'svelte/store';

export const VISIBILITY_CTX = Symbol('visibility');

export interface VisibilityCtxValue {
    visible: Readable<boolean>;
}
