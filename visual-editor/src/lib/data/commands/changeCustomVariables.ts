import { get } from 'svelte/store';
import { BaseCommand } from './base';
import { type Variable } from '../customVariables';
import type { State } from '../state';

export class ChangeCustomVariablesCommand extends BaseCommand {
    private before: Variable[];
    private after: Variable[];

    constructor(state: State, newVariables: Variable[]) {
        super();

        this.before = get(state.customVariables);
        this.after = newVariables;
    }

    undo(state: State): void {
        state.customVariables.set(this.before);
    }

    redo(state: State): void {
        state.customVariables.set(this.after);
    }

    canMerge(other: BaseCommand): boolean {
        return super.canMerge(other) && other instanceof ChangeCustomVariablesCommand &&
            this.after === other.before;
    }

    mergeMeWith(other: this): void {
        this.after = other.after;
    }

    toLangKey(): string {
        return 'commands.change_custom_variables';
    }
}
