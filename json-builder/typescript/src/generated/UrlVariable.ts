// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Variable — URL as a string.
 */
export class UrlVariable<T extends UrlVariableProps = UrlVariableProps> {
    readonly _props?: Exact<UrlVariableProps, T>;

    readonly type = 'url';
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;

    constructor(props: Exact<UrlVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

interface UrlVariableProps {
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<string>;
}
