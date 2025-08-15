// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithRawArray<T extends EntityWithRawArrayProps = EntityWithRawArrayProps> {
    readonly _props?: Exact<EntityWithRawArrayProps, T>;

    readonly type = 'entity_with_raw_array';
    array: Type<unknown[] | DivExpression>;

    constructor(props: Exact<EntityWithRawArrayProps, T>) {
        this.array = props.array;
    }
}

export interface EntityWithRawArrayProps {
    array: Type<unknown[] | DivExpression>;
}
