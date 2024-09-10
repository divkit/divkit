import { get } from 'svelte/store';
import { findLeaf, type RemoveLeafData, cascadeRemoveLeaf } from '../../utils/tree';
import type { State } from '../state';
import { BaseCommand } from './base';

export class RemoveLeafCommand extends BaseCommand {
    private leafId: string;
    private cascadeRemoval: RemoveLeafData[];

    constructor(leafId: string) {
        super();

        this.leafId = leafId;
        this.cascadeRemoval = [];
    }

    undo(state: State): void {
        const root = get(state.tree);
        for (let i = this.cascadeRemoval.length - 1; i >= 0; --i) {
            const data = this.cascadeRemoval[i];

            const parent = findLeaf(root, data.parentId);
            if (parent) {
                const leaf = data.leaf;
                parent.childs.splice(data.insertIndex, 0, leaf);
                leaf.parent = parent;
            }
        }
    }

    redo(state: State): void {
        const root = get(state.tree);
        this.cascadeRemoval = cascadeRemoveLeaf(root, this.leafId);
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge RemoveLeafCommand');
    }

    toLangKey(): string {
        return 'commands.remove_component';
    }
}
