// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithOptionalStringEnumProperty<T extends EntityWithOptionalStringEnumPropertyProps = EntityWithOptionalStringEnumPropertyProps> {
    readonly _props?: Exact<EntityWithOptionalStringEnumPropertyProps, T>;

    readonly type = 'entity_with_optional_string_enum_property';
    property?: Type<EntityWithOptionalStringEnumPropertyProperty | DivExpression>;

    constructor(props?: Exact<EntityWithOptionalStringEnumPropertyProps, T>) {
        this.property = props?.property;
    }
}

export interface EntityWithOptionalStringEnumPropertyProps {
    property?: Type<EntityWithOptionalStringEnumPropertyProperty | DivExpression>;
}

export type EntityWithOptionalStringEnumPropertyProperty =
    | 'first'
    | 'second';
