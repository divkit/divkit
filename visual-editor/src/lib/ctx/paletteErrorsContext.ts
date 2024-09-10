import type { Readable } from 'svelte/store';

export const PALETTE_ERRORS_CTX = Symbol('palette-errors');

export interface PaletteErrorsContext {
    errors: Readable<Map<string, string>>;
}
