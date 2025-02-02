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
        console.log("SetJsonCommand constructor called with newJson:", this.newJson);
    }

    undo(state: State): void {
        state.tree.set(this.oldTree);
    }

    redo(state: State): void {
        state.restoreGenId(this.genId);
        console.log("SetJsonCommand redo called with newJson before setting:", this.newJson);
        state.setDivJson(JSON.parse(this.newJson));
        console.log("SetJsonCommand redo completed with state tree:", get(state.tree));
    }

    mergeMeWith(other: this): void {
        this.newJson = other.newJson;
    }

    toLangKey(): string {
        return 'commands.set_json';
    }
}