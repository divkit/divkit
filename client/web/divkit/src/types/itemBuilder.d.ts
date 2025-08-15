import type { BooleanInt } from '../../typings/common';
import type { DivBaseData } from './base';

export interface CollectionItemBuilderPrototype {
    selector?: BooleanInt;
    div: DivBaseData;
    id?: string;
}

export interface CollectionItemBuilder {
    data: object[];
    prototypes: CollectionItemBuilderPrototype[];
    data_element_name?: string;
}
