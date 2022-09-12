// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Variable — HEX color as a string.
 */
export class ColorVariable<T extends ColorVariableProps = ColorVariableProps> {
    readonly _props?: Exact<ColorVariableProps, T>;

    readonly type = 'color';
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;

    constructor(props: Exact<ColorVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

export interface ColorVariableProps {
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;
}
