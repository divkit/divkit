import { get } from 'svelte/store';
import type { PaletteItem } from '../palette';
import { BaseCommand } from './base';
import type { State } from '../state';

function setItemName(list: PaletteItem[], id: string, name: string): PaletteItem[] {
    return list.map(it => {
        if (it.id === id) {
            return {
                ...it,
                name
            };
        }
        return it;
    });
}

export class RenamePaletteItemCommand extends BaseCommand {
    private id: string;
    private beforeName: string;
    private afterName: string;

    constructor(id: string, afterName: string) {
        super();

        this.id = id;
        this.beforeName = '';
        this.afterName = afterName;
    }

    undo(state: State): void {
        const list = get(state.palette);
        state.palette.set(setItemName(list, this.id, this.beforeName));
    }

    redo(state: State): void {
        const list = get(state.palette);
        const item = list.find(it => it.id === this.id);
        if (item) {
            this.beforeName = item.name;
            state.palette.set(setItemName(list, this.id, this.afterName));
        }
    }

    canMerge(other: BaseCommand): boolean {
        return super.canMerge(other) && other instanceof RenamePaletteItemCommand && this.id === other.id;
    }

    mergeMeWith(other: this): void {
        this.afterName = other.afterName;
    }

    toLangKey(): string {
        return 'commands.rename_palette_item';
    }
}
