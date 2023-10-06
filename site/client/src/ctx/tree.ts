import type { Writable } from 'svelte/store';

export interface TreeLeaf {
    id: string;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    props: any;
    parent?: TreeLeaf;
    childs: TreeLeaf[];
}

export type TreeGetText = (leaf: TreeLeaf) => string;

export interface TreeContext {
    collapsedStore: Writable<Record<string, boolean>>;
    selectedStore: Writable<TreeLeaf | null>;
    highlightStore: Writable<TreeLeaf | null>;
    getText: TreeGetText;
}

export const TREE_CTX = Symbol('tree');
