import { get } from 'svelte/store';
import type { TreeLeaf } from '../../ctx/tree';
import { copyValue } from '../../utils/copyValue';
import { getObjectProperty, setObjectProperty } from '../../utils/objectProperty';
import { findLeaf } from '../../utils/tree';
import { BaseCommand } from './base';
import type { State } from '../state';

export interface SetPropertyItem {
    leafId: string;
    property: string;
    value: unknown;
}

interface StoredSetPropertyItem {
    leafId: string;
    property: string;
    newValue: unknown;
    oldValue: unknown;
}

export class SetPropertyCommand extends BaseCommand {
    private changes: StoredSetPropertyItem[];

    constructor(model: TreeLeaf, changes: SetPropertyItem[]) {
        super();

        this.changes = changes.map(change => {
            const leaf = findLeaf(model, change.leafId);

            if (!leaf) {
                throw new Error('Missing leaf ' + change.leafId);
            }

            return {
                leafId: change.leafId,
                property: change.property,
                oldValue: copyValue(getObjectProperty(leaf.props.json, change.property)),
                newValue: copyValue(change.value)
            };
        });
    }

    undo(state: State): void {
        for (let i = this.changes.length - 1; i >= 0; --i) {
            const change = this.changes[i];

            const leaf = findLeaf(get(state.tree), change.leafId);

            if (!leaf) {
                throw new Error('Missing leaf ' + change.leafId);
            }

            setObjectProperty(leaf.props.json, change.property, change.oldValue);

            // redraw components with errors
            if (leaf === get(state.selectedLeaf)) {
                state.selectedLeaf.set(leaf);
            }
        }
    }

    redo(state: State): void {
        this.changes.forEach(change => {
            const leaf = findLeaf(get(state.tree), change.leafId);

            if (!leaf) {
                throw new Error('Missing leaf ' + change.leafId);
            }

            setObjectProperty(leaf.props.json, change.property, change.newValue);

            // redraw components with errors
            if (leaf === get(state.selectedLeaf)) {
                state.selectedLeaf.set(leaf);
            }
        });
    }

    canMerge(other: BaseCommand): boolean {
        if (!super.canMerge(other)) {
            return false;
        }

        const otherSet = other as SetPropertyCommand;

        return this.changes.length === 1 && otherSet.changes.length === 1 &&
            this.changes[0].leafId === otherSet.changes[0].leafId &&
            this.changes[0].property === otherSet.changes[0].property;
    }

    mergeMeWith(other: this): void {
        const newChanges = this.changes.concat(other.changes);

        let prevChange = newChanges[0];
        const mergedChanges = [prevChange];
        for (let i = 1; i < newChanges.length; ++i) {
            const newChange = newChanges[i];
            if (prevChange.leafId === newChange.leafId && prevChange.property === newChange.property) {
                prevChange.newValue = newChange.newValue;
            } else {
                prevChange = newChange;
                mergedChanges.push(prevChange);
            }
        }

        this.changes = mergedChanges;
    }

    toLangKey(): string {
        return 'commands.set_property';
    }
}
