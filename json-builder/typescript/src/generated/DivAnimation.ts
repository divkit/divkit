// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivAnimationInterpolator,
    DivCount,
} from './';

/**
 * Element animation parameters.
 */
export interface IDivAnimation {
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Final value of an animation.
     */
    end_value?: Type<number | DivExpression>;
    /**
     * Animation speed nature. When the value is set to `spring` — animation of damping fluctuations
     * cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier
     * curve:`linear` — cubic-bezier(0, 0, 1, 1);`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);`ease_in`
     * — cubic-bezier(0.42, 0, 1, 1);`ease_out` — cubic-bezier(0, 0, 0.58, 1);`ease_in_out` —
     * cubic-bezier(0.42, 0, 0.58, 1).
     */
    interpolator?: Type<DivAnimationInterpolator | DivExpression>;
    /**
     * Animation elements.
     */
    items?: Type<NonEmptyArray<IDivAnimation>>;
    /**
     * Animation type.
     */
    name: Type<DivAnimationName | DivExpression>;
    /**
     * Number of animation repetitions.
     */
    repeat?: Type<DivCount>;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number | DivExpression>;
    /**
     * Starting value of an animation.
     */
    start_value?: Type<number | DivExpression>;
}

export type DivAnimationName =
    | 'fade'
    | 'translate'
    | 'scale'
    | 'native'
    | 'set'
    | 'no_animation';
