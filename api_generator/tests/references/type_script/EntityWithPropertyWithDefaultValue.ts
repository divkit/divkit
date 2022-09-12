// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

export class EntityWithPropertyWithDefaultValue<T extends EntityWithPropertyWithDefaultValueProps = EntityWithPropertyWithDefaultValueProps> {
    readonly _props?: Exact<EntityWithPropertyWithDefaultValueProps, T>;

    readonly type = 'entity_with_property_with_default_value';
    int?: Type<number> | DivExpression;
    /**
     * non_optional is used to suppress auto-generation of default value for object with all-optional
     * fields.
     */
    nested?: Type<IEntityWithPropertyWithDefaultValueNested>;
    url?: Type<string> | DivExpression;

    constructor(props?: Exact<EntityWithPropertyWithDefaultValueProps, T>) {
        this.int = props?.int;
        this.nested = props?.nested;
        this.url = props?.url;
    }
}

export interface EntityWithPropertyWithDefaultValueProps {
    int?: Type<number> | DivExpression;
    /**
     * non_optional is used to suppress auto-generation of default value for object with all-optional
     * fields.
     */
    nested?: Type<IEntityWithPropertyWithDefaultValueNested>;
    url?: Type<string> | DivExpression;
}

/**
 * non_optional is used to suppress auto-generation of default value for object with all-optional
 * fields.
 */
export interface IEntityWithPropertyWithDefaultValueNested {
    int?: Type<number> | DivExpression;
    non_optional: Type<string> | DivExpression;
    url?: Type<string> | DivExpression;
}
