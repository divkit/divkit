// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithOptionalProperty<T extends EntityWithOptionalPropertyProps = EntityWithOptionalPropertyProps> {
    readonly _props?: Exact<EntityWithOptionalPropertyProps, T>;

    readonly type = 'entity_with_optional_property';
    property?: Type<string | DivExpression>;

    constructor(props?: Exact<EntityWithOptionalPropertyProps, T>) {
        this.property = props?.property;
    }
}

interface EntityWithOptionalPropertyProps {
    property?: Type<string | DivExpression>;
}
