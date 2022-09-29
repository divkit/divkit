// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

/**
 * Relative radius of the gradient transition.
 */
export class DivRadialGradientRelativeRadius<T extends DivRadialGradientRelativeRadiusProps = DivRadialGradientRelativeRadiusProps> {
    readonly _props?: Exact<DivRadialGradientRelativeRadiusProps, T>;

    readonly type = 'relative';
    /**
     * Type of relative radius of the gradient transition.
     */
    value: Type<DivRadialGradientRelativeRadiusValue | DivExpression>;

    constructor(props: Exact<DivRadialGradientRelativeRadiusProps, T>) {
        this.value = props.value;
    }
}

export interface DivRadialGradientRelativeRadiusProps {
    /**
     * Type of relative radius of the gradient transition.
     */
    value: Type<DivRadialGradientRelativeRadiusValue | DivExpression>;
}

export type DivRadialGradientRelativeRadiusValue =
    | 'nearest_corner'
    | 'farthest_corner'
    | 'nearest_side'
    | 'farthest_side';
