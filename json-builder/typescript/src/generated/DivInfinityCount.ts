// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Бесконечное число повторов.
 */
export class DivInfinityCount<T extends DivInfinityCountProps = DivInfinityCountProps> {
    readonly _props?: Exact<DivInfinityCountProps, T>;

    readonly type = 'infinity';

    constructor(props?: Exact<DivInfinityCountProps, T>) {
    }
}

interface DivInfinityCountProps {
}
