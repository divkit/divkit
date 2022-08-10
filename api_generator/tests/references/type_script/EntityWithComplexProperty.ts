// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithComplexProperty<T extends EntityWithComplexPropertyProps = EntityWithComplexPropertyProps> {
    readonly _props?: Exact<EntityWithComplexPropertyProps, T>;

    readonly type = 'entity_with_complex_property';
    property: Type<IEntityWithComplexPropertyProperty>;

    constructor(props: Exact<EntityWithComplexPropertyProps, T>) {
        this.property = props.property;
    }
}

interface EntityWithComplexPropertyProps {
    property: Type<IEntityWithComplexPropertyProperty>;
}

export interface IEntityWithComplexPropertyProperty {
    value: Type<string> | DivExpression;
}
