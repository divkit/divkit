import { get } from 'svelte/store';
import type { PaletteItem } from '../palette';
import { BaseCommand } from './base';
import type { State } from '../state';

export class DeletePaletteItemCommand extends BaseCommand {
    private paletteItem: PaletteItem;
    private index: number;

    constructor(item: PaletteItem) {
        super();

        this.index = -1;
        this.paletteItem = item;
    }

    undo(state: State): void {
        const list = get(state.palette).slice();
        list.splice(this.index, 0, this.paletteItem);
        state.palette.set(list);
    }

    redo(state: State): void {
        const list = get(state.palette);
        this.index = list.findIndex(it => it.id === this.paletteItem.id);
        state.palette.set(list.filter(it => it.id !== this.paletteItem.id));
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge DeletePaletteItemCommand');
    }

    toLangKey(): string {
        return 'commands.delete_palette_item';
    }
}
