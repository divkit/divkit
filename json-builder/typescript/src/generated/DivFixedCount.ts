// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Fixed number of repetitions.
 */
export class DivFixedCount<T extends DivFixedCountProps = DivFixedCountProps> {
    readonly _props?: Exact<DivFixedCountProps, T>;

    readonly type = 'fixed';
    /**
     * Number of repetitions.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivFixedCountProps, T>) {
        this.value = props.value;
    }
}

interface DivFixedCountProps {
    /**
     * Number of repetitions.
     */
    value: Type<number> | DivExpression;
}
