// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Целочисленная переменная.
 */
export class IntegerVariable<T extends IntegerVariableProps = IntegerVariableProps> {
    readonly _props?: Exact<IntegerVariableProps, T>;

    readonly type = 'integer';
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<number>;

    constructor(props: Exact<IntegerVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface IntegerVariableProps {
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<number>;
}
