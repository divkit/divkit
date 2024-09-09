import { get } from 'svelte/store';
import type { PaletteItem } from '../palette';
import { BaseCommand } from './base';
import type { State } from '../state';

function orderItems(list: PaletteItem[], order: string[]): PaletteItem[] {
    const map: Record<string, PaletteItem> = {};
    list.forEach(item => {
        map[item.id] = item;
    });
    return order.map(id => map[id]);
}

export class ReorderPaletteItemCommand extends BaseCommand {
    private beforeOrder: string[];
    private afterOrder: string[];

    constructor(afterOrder: string[]) {
        super();

        this.beforeOrder = [];
        this.afterOrder = afterOrder;
    }

    undo(state: State): void {
        const list = get(state.palette);
        state.palette.set(orderItems(list, this.beforeOrder));
    }

    redo(state: State): void {
        const list = get(state.palette);
        this.beforeOrder = list.map(it => it.id);
        state.palette.set(orderItems(list, this.afterOrder));
    }

    mergeMeWith(other: this): void {
        this.afterOrder = other.afterOrder;
    }

    toLangKey(): string {
        return 'commands.reorder_palette_item';
    }
}
