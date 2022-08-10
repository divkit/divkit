// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Transparency animation.
 */
export class DivFadeTransition<T extends DivFadeTransitionProps = DivFadeTransitionProps> {
    readonly _props?: Exact<DivFadeTransitionProps, T>;

    readonly type = 'fade';
    /**
     * Value of the alpha channel which the element starts appearing from or at which it finishes
     * disappearing.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number> | DivExpression;

    constructor(props?: Exact<DivFadeTransitionProps, T>) {
        this.alpha = props?.alpha;
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

interface DivFadeTransitionProps {
    /**
     * Value of the alpha channel which the element starts appearing from or at which it finishes
     * disappearing.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number> | DivExpression;
}
