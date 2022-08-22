// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithStringArrayProperty<T extends EntityWithStringArrayPropertyProps = EntityWithStringArrayPropertyProps> {
    readonly _props?: Exact<EntityWithStringArrayPropertyProps, T>;

    readonly type = 'entity_with_string_array_property';
    array: Type<NonEmptyArray<string | DivExpression>>;

    constructor(props: Exact<EntityWithStringArrayPropertyProps, T>) {
        this.array = props.array;
    }
}

interface EntityWithStringArrayPropertyProps {
    array: Type<NonEmptyArray<string | DivExpression>>;
}
