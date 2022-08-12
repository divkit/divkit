// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Переменная с плавающей точкой.
 */
export class NumberVariable<T extends NumberVariableProps = NumberVariableProps> {
    readonly _props?: Exact<NumberVariableProps, T>;

    readonly type = 'number';
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<number>;

    constructor(props: Exact<NumberVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface NumberVariableProps {
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<number>;
}
