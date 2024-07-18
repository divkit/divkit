import type { Node } from './ast';

/**
 * LRU cache
 * JavaScript Map iteration is guaranteed to be performed in the insertation order
 * On usage, remove the item from the Map and insert it in the end
 */

const MAX_ENTRIES = 128;

const cache = new Map();
let latestItem: string | undefined;

export function cacheGet(expr: string): Node | undefined {
    return cache.get(expr);
}

export function cacheSet(expr: string, ast: Node): void {
    if (expr === latestItem) {
        return;
    }

    cache.delete(expr);

    if (cache.size >= MAX_ENTRIES) {
        cache.delete(cache.keys().next().value);
    }

    cache.set(expr, ast);

    latestItem = expr;
}
