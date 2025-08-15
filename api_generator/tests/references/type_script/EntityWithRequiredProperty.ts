// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithRequiredProperty<T extends EntityWithRequiredPropertyProps = EntityWithRequiredPropertyProps> {
    readonly _props?: Exact<EntityWithRequiredPropertyProps, T>;

    readonly type = 'entity_with_required_property';
    property: Type<string | DivExpression>;

    constructor(props: Exact<EntityWithRequiredPropertyProps, T>) {
        this.property = props.property;
    }
}

export interface EntityWithRequiredPropertyProps {
    property: Type<string | DivExpression>;
}
