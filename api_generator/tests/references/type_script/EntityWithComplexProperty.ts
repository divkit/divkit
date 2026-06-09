// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithComplexProperty<T extends EntityWithComplexPropertyProps = EntityWithComplexPropertyProps> {
    readonly _props?: Exact<EntityWithComplexPropertyProps, T>;

    readonly type = 'entity_with_complex_property';
    property: Type<IEntityWithComplexPropertyComplexProperty>;

    constructor(props: Exact<EntityWithComplexPropertyProps, T>) {
        this.property = props.property;
    }
}

export interface EntityWithComplexPropertyProps {
    property: Type<IEntityWithComplexPropertyComplexProperty>;
}

export interface IEntityWithComplexPropertyComplexProperty {
    value: Type<string | DivExpression>;
}
