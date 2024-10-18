import type { Readable } from 'svelte/store';

export const ENABLED_CTX = Symbol('enabled');

export interface EnabledCtxValue {
    isEnabled: Readable<boolean>;
}
