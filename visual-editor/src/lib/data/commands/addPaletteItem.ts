import { get } from 'svelte/store';
import type { PaletteItem } from '../palette';
import { BaseCommand } from './base';
import type { State } from '../state';

export class AddPaletteItemCommand extends BaseCommand {
    private id: string;
    private paletteItem: PaletteItem | null = null;
    private index: number | null = null;

    constructor(id: string) {
        super();

        this.id = id;
    }

    undo(state: State): void {
        const list = get(state.palette);
        this.index = list.findIndex(it => it.id === this.id);
        const item = list[this.index];
        this.paletteItem = item;
        state.palette.set(list.filter(it => it.id !== this.id));
    }

    redo(state: State): void {
        const id = this.id;
        const item = this.paletteItem || (this.paletteItem = {
            id,
            name: id,
            light: '#fff',
            dark: '#000'
        });
        const list = get(state.palette).slice();
        if (this.index === null) {
            list.push(item);
        } else {
            list.splice(this.index, 0, item);
        }
        state.palette.set(list);
    }

    canMerge(_other: BaseCommand): boolean {
        return false;
    }

    mergeMeWith(_other: this): void {
        throw new Error('Cannot merge AddPaletteItemCommand');
    }

    toLangKey(): string {
        return 'commands.add_palette_item';
    }
}
