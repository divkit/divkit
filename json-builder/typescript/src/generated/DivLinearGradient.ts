// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Linear gradient.
 */
export class DivLinearGradient<T extends DivLinearGradientProps = DivLinearGradientProps> {
    readonly _props?: Exact<DivLinearGradientProps, T>;

    readonly type = 'gradient';
    /**
     * Angle of gradient direction.
     */
    angle?: Type<number | DivExpression>;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;

    constructor(props: Exact<DivLinearGradientProps, T>) {
        this.angle = props.angle;
        this.colors = props.colors;
    }
}

export interface DivLinearGradientProps {
    /**
     * Angle of gradient direction.
     */
    angle?: Type<number | DivExpression>;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;
}
