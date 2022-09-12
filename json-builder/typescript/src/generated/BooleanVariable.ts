// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * A Boolean variable in binary format.
 */
export class BooleanVariable<T extends BooleanVariableProps = BooleanVariableProps> {
    readonly _props?: Exact<BooleanVariableProps, T>;

    readonly type = 'boolean';
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<IntBoolean>;

    constructor(props: Exact<BooleanVariableProps, T>) {
        this.name = props.name;
        this.value = props.value;
    }
}

export interface BooleanVariableProps {
    /**
     * Variable name.
     */
    name: Type<string>;
    /**
     * Value.
     */
    value: Type<IntBoolean>;
}
