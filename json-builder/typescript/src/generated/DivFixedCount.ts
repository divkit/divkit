// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Фиксированное число повторов.
 */
export class DivFixedCount<T extends DivFixedCountProps = DivFixedCountProps> {
    readonly _props?: Exact<DivFixedCountProps, T>;

    readonly type = 'fixed';
    /**
     * Число повторов.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivFixedCountProps, T>) {
        this.value = props.value;
    }
}

interface DivFixedCountProps {
    /**
     * Число повторов.
     */
    value: Type<number> | DivExpression;
}
