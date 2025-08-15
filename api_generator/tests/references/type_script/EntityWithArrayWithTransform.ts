// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithArrayWithTransform<T extends EntityWithArrayWithTransformProps = EntityWithArrayWithTransformProps> {
    readonly _props?: Exact<EntityWithArrayWithTransformProps, T>;

    readonly type = 'entity_with_array_with_transform';
    array: Type<NonEmptyArray<string | DivExpression>>;

    constructor(props: Exact<EntityWithArrayWithTransformProps, T>) {
        this.array = props.array;
    }
}

export interface EntityWithArrayWithTransformProps {
    array: Type<NonEmptyArray<string | DivExpression>>;
}
