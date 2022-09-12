// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

import {
    Entity,
} from './';

export class EntityWithArray<T extends EntityWithArrayProps = EntityWithArrayProps> {
    readonly _props?: Exact<EntityWithArrayProps, T>;

    readonly type = 'entity_with_array';
    array: Type<NonEmptyArray<Entity>>;

    constructor(props: Exact<EntityWithArrayProps, T>) {
        this.array = props.array;
    }
}

export interface EntityWithArrayProps {
    array: Type<NonEmptyArray<Entity>>;
}
