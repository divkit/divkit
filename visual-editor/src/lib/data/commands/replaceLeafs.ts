import { get } from 'svelte/store';
import type { TreeLeaf } from '../../ctx/tree';
import { findLeaf, structuredCopyLeaf } from '../../utils/tree';
import type { State } from '../state';
import { BaseCommand } from './base';

interface ReplaceLeafsConfig {
    parentId: string;
    leafs: TreeLeaf[];
}

export class ReplaceLeafsCommand extends BaseCommand {
    private config: ReplaceLeafsConfig;
    private oldLeafs: TreeLeaf[] = [];

    constructor(config: ReplaceLeafsConfig) {
        super();

        this.config = config;
    }

    undo(state: State): void {
        const parent = findLeaf(get(state.tree), this.config.parentId);

        if (!parent) {
            return;
        }

        const leafs = this.oldLeafs;

        leafs.forEach(leaf => {
            leaf.parent = parent;
        });

        parent.childs = leafs;
    }

    redo(state: State): void {
        const parent = findLeaf(get(state.tree), this.config.parentId);

        if (!parent) {
            return;
        }

        const leafs = this.config.leafs.map(it => structuredCopyLeaf(it));

        leafs.forEach(leaf => {
            leaf.parent = parent;
        });

        this.oldLeafs = parent.childs;
        parent.childs = leafs;
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge ReplaceLeafsCommand');
    }

    toLangKey(): string {
        return 'commands.replace_components';
    }
}
