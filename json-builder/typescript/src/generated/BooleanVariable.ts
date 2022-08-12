// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Логическая переменная.
 */
export class BooleanVariable<T extends BooleanVariableProps = BooleanVariableProps> {
    readonly _props?: Exact<BooleanVariableProps, T>;

    readonly type = 'boolean';
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<IntBoolean>;

    constructor(props: Exact<BooleanVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface BooleanVariableProps {
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<IntBoolean>;
}
