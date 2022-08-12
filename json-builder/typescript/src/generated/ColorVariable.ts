// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Переменная — цвет в формате HEX, представленный в виде строки.
 */
export class ColorVariable<T extends ColorVariableProps = ColorVariableProps> {
    readonly _props?: Exact<ColorVariableProps, T>;

    readonly type = 'color';
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<string>;

    constructor(props: Exact<ColorVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface ColorVariableProps {
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<string>;
}
