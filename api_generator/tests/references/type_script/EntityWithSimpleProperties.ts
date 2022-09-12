// Generated code. Do not modify.

import { TemplateBlock } from '../blocks';
import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { Type } from '../template';

/**
 * Entity with simple properties.
 */
export class EntityWithSimpleProperties<T extends EntityWithSimplePropertiesProps = EntityWithSimplePropertiesProps> {
    readonly _props?: Exact<EntityWithSimplePropertiesProps, T>;

    readonly type = 'entity_with_simple_properties';
    /**
     * Boolean property.
     */
    boolean?: Type<boolean> | DivExpression;
    /**
     * Boolean value in numeric format.
     *
     * @deprecated
     */
    boolean_int?: Type<IntBoolean> | DivExpression;
    /**
     * Color.
     */
    color?: Type<string> | DivExpression;
    /**
     * Floating point number.
     */
    double?: Type<number> | DivExpression;
    /**
     * ID. Can't contain expressions.
     */
    id?: Type<number>;
    /**
     * Integer.
     */
    integer?: Type<number> | DivExpression;
    /**
     * Positive integer.
     */
    positive_integer?: Type<number> | DivExpression;
    /**
     * String.
     */
    string?: Type<string> | DivExpression;
    url?: Type<string> | DivExpression;

    constructor(props?: Exact<EntityWithSimplePropertiesProps, T>) {
        this.boolean = props?.boolean;
        this.boolean_int = props?.boolean_int;
        this.color = props?.color;
        this.double = props?.double;
        this.id = props?.id;
        this.integer = props?.integer;
        this.positive_integer = props?.positive_integer;
        this.string = props?.string;
        this.url = props?.url;
    }
}

export interface EntityWithSimplePropertiesProps {
    /**
     * Boolean property.
     */
    boolean?: Type<boolean> | DivExpression;
    /**
     * Boolean value in numeric format.
     *
     * @deprecated
     */
    boolean_int?: Type<IntBoolean> | DivExpression;
    /**
     * Color.
     */
    color?: Type<string> | DivExpression;
    /**
     * Floating point number.
     */
    double?: Type<number> | DivExpression;
    /**
     * ID. Can't contain expressions.
     */
    id?: Type<number>;
    /**
     * Integer.
     */
    integer?: Type<number> | DivExpression;
    /**
     * Positive integer.
     */
    positive_integer?: Type<number> | DivExpression;
    /**
     * String.
     */
    string?: Type<string> | DivExpression;
    url?: Type<string> | DivExpression;
}
