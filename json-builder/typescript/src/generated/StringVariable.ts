// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * A string variable.
 */
export class StringVariable<T extends StringVariableProps = StringVariableProps> {
    readonly _props?: Exact<StringVariableProps, T>;

    readonly type = 'string';
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;

    constructor(props: Exact<StringVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface StringVariableProps {
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;
}
