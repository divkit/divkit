import { get } from 'svelte/store';
import type { TreeLeaf } from '../../ctx/tree';
import { copyValue } from '../../utils/copyValue';
import { getObjectProperty, setObjectProperty } from '../../utils/objectProperty';
import { cascadeRemoveLeaf, findLeaf, structuredCopyLeaf } from '../../utils/tree';
import type { State } from '../state';
import { BaseCommand } from './base';

interface MoveLeafConfig {
    leafId: string;
    newParentId: string;
    insertIndex: number;
    changes?: {
        property: string;
        value: unknown;
    }[];
}

interface RemoveLeafData {
    parentId: string;
    insertIndex: number;
    leaf: TreeLeaf
}

interface StoredSetPropertyItem {
    leafId: string;
    property: string;
    newValue: unknown;
    oldValue: unknown;
}

export class MoveLeafCommand extends BaseCommand {
    private config: MoveLeafConfig;
    private changes: StoredSetPropertyItem[] | undefined;
    private cascadeRemoval: RemoveLeafData[];

    constructor(state: State, config: MoveLeafConfig) {
        super();

        this.config = config;
        this.cascadeRemoval = [];

        if (config.changes) {
            this.changes = config.changes.map(change => {
                const leaf = findLeaf(get(state.tree), config.leafId);

                if (!leaf) {
                    throw new Error('Missing leaf ' + config.leafId);
                }

                return {
                    leafId: config.leafId,
                    property: change.property,
                    oldValue: copyValue(getObjectProperty(leaf.props.json, change.property)),
                    newValue: copyValue(change.value)
                };
            });
        }
    }

    undo(state: State): void {
        const root = get(state.tree);
        const parent = findLeaf(root, this.config.newParentId);
        if (!parent) {
            return;
        }

        parent.childs = parent.childs.filter(it => it.id !== this.config.leafId);

        for (let i = this.cascadeRemoval.length - 1; i >= 0; --i) {
            const data = this.cascadeRemoval[i];

            const parent = findLeaf(root, data.parentId);
            if (parent) {
                const leaf = data.leaf;
                parent.childs.splice(data.insertIndex, 0, leaf);
                leaf.parent = parent;
            }
        }

        if (this.changes) {
            const leaf = findLeaf(root, this.config.leafId);
            if (leaf) {
                this.changes.forEach(change => {
                    setObjectProperty(leaf.props.json, change.property, change.oldValue);
                });
            }
        }
    }

    redo(state: State): void {
        const root = get(state.tree);
        const parent = findLeaf(root, this.config.newParentId);
        const origLeaf = findLeaf(root, this.config.leafId);

        if (!parent || !origLeaf) {
            return;
        }

        const leaf = structuredCopyLeaf(origLeaf);

        this.cascadeRemoval = cascadeRemoveLeaf(root, this.config.leafId, !(origLeaf.parent?.id === parent.id));

        leaf.parent = parent;
        parent.childs.splice(this.config.insertIndex, 0, leaf);

        if (this.changes) {
            this.changes.forEach(change => {
                setObjectProperty(leaf.props.json, change.property, change.newValue);
            });
        }
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge MoveLeafCommand');
    }

    toLangKey(): string {
        return 'commands.move_component';
    }
}
