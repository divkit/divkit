// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Gradient background.
 */
export class DivGradientBackground<T extends DivGradientBackgroundProps = DivGradientBackgroundProps> {
    readonly _props?: Exact<DivGradientBackgroundProps, T>;

    readonly type = 'gradient';
    /**
     * Angle of gradient direction.
     */
    angle?: Type<number> | DivExpression;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;

    constructor(props: Exact<DivGradientBackgroundProps, T>) {
        this.angle = props.angle;
        this.colors = props.colors;
    }
}

interface DivGradientBackgroundProps {
    /**
     * Angle of gradient direction.
     */
    angle?: Type<number> | DivExpression;
    /**
     * Colors. Gradient points will be located at an equal distance from each other.
     */
    colors: Type<NonEmptyArray<string | DivExpression>>;
}
