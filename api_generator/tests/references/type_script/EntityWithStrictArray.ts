// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

import {
    Entity,
} from './';

export class EntityWithStrictArray<T extends EntityWithStrictArrayProps = EntityWithStrictArrayProps> {
    readonly _props?: Exact<EntityWithStrictArrayProps, T>;

    readonly type = 'entity_with_strict_array';
    array: Type<NonEmptyArray<Entity>>;

    constructor(props: Exact<EntityWithStrictArrayProps, T>) {
        this.array = props.array;
    }
}

export interface EntityWithStrictArrayProps {
    array: Type<NonEmptyArray<Entity>>;
}
