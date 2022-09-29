// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

/**
 * Relative central point of the gradient.
 */
export class DivRadialGradientRelativeCenter<T extends DivRadialGradientRelativeCenterProps = DivRadialGradientRelativeCenterProps> {
    readonly _props?: Exact<DivRadialGradientRelativeCenterProps, T>;

    readonly type = 'relative';
    /**
     * Shift value of the central relative point of the gradient in the range `0..1`.
     */
    value: Type<number | DivExpression>;

    constructor(props: Exact<DivRadialGradientRelativeCenterProps, T>) {
        this.value = props.value;
    }
}

export interface DivRadialGradientRelativeCenterProps {
    /**
     * Shift value of the central relative point of the gradient in the range `0..1`.
     */
    value: Type<number | DivExpression>;
}
