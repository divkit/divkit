// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithStringEnumPropertyWithDefaultValue<T extends EntityWithStringEnumPropertyWithDefaultValueProps = EntityWithStringEnumPropertyWithDefaultValueProps> {
    readonly _props?: Exact<EntityWithStringEnumPropertyWithDefaultValueProps, T>;

    readonly type = 'entity_with_string_enum_property_with_default_value';
    value?: Type<EntityWithStringEnumPropertyWithDefaultValueValue> | DivExpression;

    constructor(props?: Exact<EntityWithStringEnumPropertyWithDefaultValueProps, T>) {
        this.value = props?.value;
    }
}

interface EntityWithStringEnumPropertyWithDefaultValueProps {
    value?: Type<EntityWithStringEnumPropertyWithDefaultValueValue> | DivExpression;
}

export type EntityWithStringEnumPropertyWithDefaultValueValue =
    | 'first'
    | 'second'
    | 'third';
