// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

/**
 * An integer variable.
 */
export class IntegerVariable<T extends IntegerVariableProps = IntegerVariableProps> {
    readonly _props?: Exact<IntegerVariableProps, T>;

    readonly type = 'integer';
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<number>;

    constructor(props: Exact<IntegerVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

export interface IntegerVariableProps {
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<number>;
}
