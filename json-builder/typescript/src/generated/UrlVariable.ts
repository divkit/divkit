// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Переменная — URL в виде строки.
 */
export class UrlVariable<T extends UrlVariableProps = UrlVariableProps> {
    readonly _props?: Exact<UrlVariableProps, T>;

    readonly type = 'url';
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<string>;

    constructor(props: Exact<UrlVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface UrlVariableProps {
    /**
     * Название переменной.
     */
    name: Type<string>;
    /**
     * Значение.
     */
    value: Type<string>;
}
