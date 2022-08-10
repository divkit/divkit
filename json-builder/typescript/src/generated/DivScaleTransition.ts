// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Scale animation.
 */
export class DivScaleTransition<T extends DivScaleTransitionProps = DivScaleTransitionProps> {
    readonly _props?: Exact<DivScaleTransitionProps, T>;

    readonly type = 'scale';
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Relative coordinate `X` of the point that won't change its position in case of scaling.
     */
    pivot_x?: Type<number> | DivExpression;
    /**
     * Relative coordinate `Y` of the point that won't change its position in case of scaling.
     */
    pivot_y?: Type<number> | DivExpression;
    /**
     * Value of the scale  from which the element starts appearing or at which it finishes
     * disappearing.
     */
    scale?: Type<number> | DivExpression;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number> | DivExpression;

    constructor(props?: Exact<DivScaleTransitionProps, T>) {
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.pivot_x = props?.pivot_x;
        this.pivot_y = props?.pivot_y;
        this.scale = props?.scale;
        this.start_delay = props?.start_delay;
    }
}

interface DivScaleTransitionProps {
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Relative coordinate `X` of the point that won't change its position in case of scaling.
     */
    pivot_x?: Type<number> | DivExpression;
    /**
     * Relative coordinate `Y` of the point that won't change its position in case of scaling.
     */
    pivot_y?: Type<number> | DivExpression;
    /**
     * Value of the scale  from which the element starts appearing or at which it finishes
     * disappearing.
     */
    scale?: Type<number> | DivExpression;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number> | DivExpression;
}
