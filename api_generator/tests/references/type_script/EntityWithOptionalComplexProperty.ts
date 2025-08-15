// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithOptionalComplexProperty<T extends EntityWithOptionalComplexPropertyProps = EntityWithOptionalComplexPropertyProps> {
    readonly _props?: Exact<EntityWithOptionalComplexPropertyProps, T>;

    readonly type = 'entity_with_optional_complex_property';
    property?: Type<IEntityWithOptionalComplexPropertyProperty>;

    constructor(props?: Exact<EntityWithOptionalComplexPropertyProps, T>) {
        this.property = props?.property;
    }
}

export interface EntityWithOptionalComplexPropertyProps {
    property?: Type<IEntityWithOptionalComplexPropertyProperty>;
}

export interface IEntityWithOptionalComplexPropertyProperty {
    value: Type<string | DivExpression>;
}
