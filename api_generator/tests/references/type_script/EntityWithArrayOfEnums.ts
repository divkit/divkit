// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithArrayOfEnums<T extends EntityWithArrayOfEnumsProps = EntityWithArrayOfEnumsProps> {
    readonly _props?: Exact<EntityWithArrayOfEnumsProps, T>;

    readonly type = 'entity_with_array_of_enums';
    items: Type<NonEmptyArray<EntityWithArrayOfEnumsItem>>;

    constructor(props: Exact<EntityWithArrayOfEnumsProps, T>) {
        this.items = props.items;
    }
}

export interface EntityWithArrayOfEnumsProps {
    items: Type<NonEmptyArray<EntityWithArrayOfEnumsItem>>;
}

export type EntityWithArrayOfEnumsItem =
    | 'first'
    | 'second';
