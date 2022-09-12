// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Solid background color.
 */
export class DivSolidBackground<T extends DivSolidBackgroundProps = DivSolidBackgroundProps> {
    readonly _props?: Exact<DivSolidBackgroundProps, T>;

    readonly type = 'solid';
    /**
     * Color.
     */
    color: Type<string> | DivExpression;

    constructor(props: Exact<DivSolidBackgroundProps, T>) {
        this.color = props.color;
    }
}

export interface DivSolidBackgroundProps {
    /**
     * Color.
     */
    color: Type<string> | DivExpression;
}
