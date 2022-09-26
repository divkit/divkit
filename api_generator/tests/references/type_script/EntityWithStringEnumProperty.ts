// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithStringEnumProperty<T extends EntityWithStringEnumPropertyProps = EntityWithStringEnumPropertyProps> {
    readonly _props?: Exact<EntityWithStringEnumPropertyProps, T>;

    readonly type = 'entity_with_string_enum_property';
    property: Type<EntityWithStringEnumPropertyProperty | DivExpression>;

    constructor(props: Exact<EntityWithStringEnumPropertyProps, T>) {
        this.property = props.property;
    }
}

export interface EntityWithStringEnumPropertyProps {
    property: Type<EntityWithStringEnumPropertyProperty | DivExpression>;
}

export type EntityWithStringEnumPropertyProperty =
    | 'first'
    | 'second';
