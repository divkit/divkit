import type { DivAction } from '../../types/divjson';

export interface ItemStates {
    div: {
        type: string;
        __leafId?: string;
    };
    state_id: string;
}

export interface ItemTabs {
    div: {
        type: string;
        __leafId?: string;
    };
    title: string;
    title_click_action?: DivAction;
}

export type Item = ItemStates | ItemTabs;
