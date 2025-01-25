import type { State } from '../state';

export abstract class BaseCommand {
    protected _ts: number;

    constructor() {
        this._ts = Date.now();
    }

    abstract redo(state: State): void;

    abstract undo(state: State): void;

    canMerge(other: BaseCommand): boolean {
        return other.constructor === this.constructor && Math.abs(this._ts - other._ts) < 10000;
    }

    abstract mergeMeWith(other: typeof this): void;

    abstract toLangKey(): string;
}
