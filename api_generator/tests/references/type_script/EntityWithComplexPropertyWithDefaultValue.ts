// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithComplexPropertyWithDefaultValue<T extends EntityWithComplexPropertyWithDefaultValueProps = EntityWithComplexPropertyWithDefaultValueProps> {
    readonly _props?: Exact<EntityWithComplexPropertyWithDefaultValueProps, T>;

    readonly type = 'entity_with_complex_property_with_default_value';
    property?: Type<IEntityWithComplexPropertyWithDefaultValueComplexProperty>;

    constructor(props?: Exact<EntityWithComplexPropertyWithDefaultValueProps, T>) {
        this.property = props?.property;
    }
}

export interface EntityWithComplexPropertyWithDefaultValueProps {
    property?: Type<IEntityWithComplexPropertyWithDefaultValueComplexProperty>;
}

export interface IEntityWithComplexPropertyWithDefaultValueComplexProperty {
    value: Type<string | DivExpression>;
}
