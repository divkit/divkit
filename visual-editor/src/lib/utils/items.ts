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
}

export type Item = ItemStates | ItemTabs;
