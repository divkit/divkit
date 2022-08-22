// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithComplexPropertyWithDefaultValue<T extends EntityWithComplexPropertyWithDefaultValueProps = EntityWithComplexPropertyWithDefaultValueProps> {
    readonly _props?: Exact<EntityWithComplexPropertyWithDefaultValueProps, T>;

    readonly type = 'entity_with_complex_property_with_default_value';
    property?: Type<IEntityWithComplexPropertyWithDefaultValueProperty>;

    constructor(props?: Exact<EntityWithComplexPropertyWithDefaultValueProps, T>) {
        this.property = props?.property;
    }
}

interface EntityWithComplexPropertyWithDefaultValueProps {
    property?: Type<IEntityWithComplexPropertyWithDefaultValueProperty>;
}

export interface IEntityWithComplexPropertyWithDefaultValueProperty {
    value: Type<string | DivExpression>;
}
