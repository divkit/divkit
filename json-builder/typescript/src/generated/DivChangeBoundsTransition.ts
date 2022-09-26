// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Element position and size change animation.
 */
export class DivChangeBoundsTransition<T extends DivChangeBoundsTransitionProps = DivChangeBoundsTransitionProps> {
    readonly _props?: Exact<DivChangeBoundsTransitionProps, T>;

    readonly type = 'change_bounds';
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator | DivExpression>;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number | DivExpression>;

    constructor(props?: Exact<DivChangeBoundsTransitionProps, T>) {
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

export interface DivChangeBoundsTransitionProps {
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator | DivExpression>;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number | DivExpression>;
}
