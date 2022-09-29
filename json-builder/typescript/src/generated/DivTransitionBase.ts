// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivAnimationInterpolator,
} from './';

export interface IDivTransitionBase {
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
