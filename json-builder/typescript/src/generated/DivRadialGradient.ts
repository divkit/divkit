// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Radial gradient.
 */
export class DivRadialGradient<T extends DivRadialGradientProps = DivRadialGradientProps> {
    readonly _props?: Exact<DivRadialGradientProps, T>;

    readonly type = 'radial_gradient';
    /**
     * Shift of the central point of the gradient relative to the left edge along the X axis in the
     * range `0..1`.
     */
    center_x?: Type<number> | DivExpression;
    /**
     * Shift of the central point of the gradient relative to the upper edge along the Y axis in the
     * range `0..1`.
     */
    center_y?: Type<number> | DivExpression;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;
    /**
     * Radius of the gradient transition in `dp`.
     */
    radius: Type<number> | DivExpression;

    constructor(props: Exact<DivRadialGradientProps, T>) {
        this.center_x = props.center_x;
        this.center_y = props.center_y;
        this.colors = props.colors;
        this.radius = props.radius;
    }
}

export interface DivRadialGradientProps {
    /**
     * Shift of the central point of the gradient relative to the left edge along the X axis in the
     * range `0..1`.
     */
    center_x?: Type<number> | DivExpression;
    /**
     * Shift of the central point of the gradient relative to the upper edge along the Y axis in the
     * range `0..1`.
     */
    center_y?: Type<number> | DivExpression;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;
    /**
     * Radius of the gradient transition in `dp`.
     */
    radius: Type<number> | DivExpression;
}
