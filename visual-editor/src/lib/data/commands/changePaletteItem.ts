import { get } from 'svelte/store';
import type { PaletteItem } from '../palette';
import { BaseCommand } from './base';
import type { State } from '../state';

function setItemColor(list: PaletteItem[], id: string, theme: 'light' | 'dark', color: string): PaletteItem[] {
    return list.map(it => {
        if (it.id === id) {
            return {
                ...it,
                [theme]: color
            };
        }
        return it;
    });
}

export class ChangePaletteItemCommand extends BaseCommand {
    private id: string;
    private theme: 'light' | 'dark';
    private beforeColor: string;
    private afterColor: string;

    constructor(id: string, theme: 'light' | 'dark', afterColor: string) {
        super();

        this.id = id;
        this.theme = theme;
        this.beforeColor = '';
        this.afterColor = afterColor;
    }

    undo(state: State): void {
        const list = get(state.palette);
        state.palette.set(setItemColor(list, this.id, this.theme, this.beforeColor));
    }

    redo(state: State): void {
        const list = get(state.palette);
        const item = list.find(it => it.id === this.id);
        if (item) {
            this.beforeColor = item[this.theme];
            state.palette.set(setItemColor(list, this.id, this.theme, this.afterColor));
        }
    }

    canMerge(other: BaseCommand): boolean {
        return super.canMerge(other) && other instanceof ChangePaletteItemCommand &&
            this.id === other.id && this.theme === other.theme;
    }

    mergeMeWith(other: this): void {
        this.afterColor = other.afterColor;
    }

    toLangKey(): string {
        return 'commands.change_palette_item';
    }
}
