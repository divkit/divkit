import { get } from 'svelte/store';
import type { TreeLeaf } from '../../ctx/tree';
import { findLeaf, structuredCopyLeaf } from '../../utils/tree';
import type { State } from '../state';
import { BaseCommand } from './base';

interface AddLeafConfig {
    parentId: string;
    insertIndex: number;
    leaf: TreeLeaf;
}

export class AddLeafCommand extends BaseCommand {
    private config: AddLeafConfig;

    constructor(config: AddLeafConfig) {
        super();
        this.config = config;
        console.log("AddLeafCommand constructor called with config:", this.config);
    }

    undo(state: State): void {
        const leafId = this.config.leaf.id;
        const leaf = findLeaf(get(state.tree), leafId);
        if (leaf?.parent) {
            console.log("AddLeafCommand undo called with leaf before removing:", leaf);
            leaf.parent.childs = leaf.parent.childs.filter(it => it.id !== leafId);
            console.log("AddLeafCommand undo completed with state tree:", get(state.tree));
        }
    }

    redo(state: State): void {
        const parent = findLeaf(get(state.tree), this.config.parentId);

        if (!parent) {
            return;
        }

        const leaf = structuredCopyLeaf(this.config.leaf);
        leaf.parent = parent;

        console.log("AddLeafCommand redo called with leaf before adding:", leaf);
        parent.childs.splice(this.config.insertIndex, 0, leaf);
        console.log("AddLeafCommand redo completed with state tree:", get(state.tree));
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge AddLeafCommand');
    }

    toLangKey(): string {
        return 'commands.add_component';
    }
}
