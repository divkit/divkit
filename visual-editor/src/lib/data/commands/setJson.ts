import { get } from 'svelte/store';
import type { TreeLeaf } from '../../ctx/tree';
import type { State } from '../state';
import { BaseCommand } from './base';

export class SetJsonCommand extends BaseCommand {
    private oldTree: TreeLeaf;
    private newJson: string;
    private genId: number;

    constructor(state: State, json: string) {
        super();

        this.oldTree = get(state.tree);
        this.newJson = json;
        this.genId = state.currentGenId();
    }

    undo(state: State): void {
        state.tree.set(this.oldTree);
    }

    redo(state: State): void {
        state.restoreGenId(this.genId);

        state.setDivJson(JSON.parse(this.newJson));
    }

    mergeMeWith(other: this): void {
        this.newJson = other.newJson;
    }

    toLangKey(): string {
        return 'commands.set_json';
    }
}
