import { get } from 'svelte/store';
import { BaseCommand } from './base';
import { type Timer } from '../timers';
import type { State } from '../state';

export class ChangeTimersCommand extends BaseCommand {
    private before: Timer[];
    private after: Timer[];

    constructor(state: State, newTimers: Timer[]) {
        super();

        this.before = get(state.timers);
        this.after = newTimers;
    }

    undo(state: State): void {
        state.timers.set(this.before);
    }

    redo(state: State): void {
        state.timers.set(this.after);
    }

    canMerge(other: BaseCommand): boolean {
        return super.canMerge(other) && other instanceof ChangeTimersCommand &&
            this.after === other.before;
    }

    mergeMeWith(other: this): void {
        this.after = other.after;
    }

    toLangKey(): string {
        return 'commands.change_timers';
    }
}
