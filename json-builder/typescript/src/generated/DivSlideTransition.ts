// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivAnimationInterpolator,
    IDivDimension,
} from './';

/**
 * Slide animation.
 */
export class DivSlideTransition<T extends DivSlideTransitionProps = DivSlideTransitionProps> {
    readonly _props?: Exact<DivSlideTransitionProps, T>;

    readonly type = 'slide';
    /**
     * A fixed value of an offset which the element starts appearing from or at which it finishes
     * disappearing. If no value is specified, the distance to the selected edge of a parent element
     * is used.
     */
    distance?: Type<IDivDimension>;
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Edge of a parent element for one of the action types:where the element will move from when
     * appearing;where the element will move to when disappearing.
     */
    edge?: Type<DivSlideTransitionEdge | DivExpression>;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator | DivExpression>;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number | DivExpression>;

    constructor(props?: Exact<DivSlideTransitionProps, T>) {
        this.distance = props?.distance;
        this.duration = props?.duration;
        this.edge = props?.edge;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

export interface DivSlideTransitionProps {
    /**
     * A fixed value of an offset which the element starts appearing from or at which it finishes
     * disappearing. If no value is specified, the distance to the selected edge of a parent element
     * is used.
     */
    distance?: Type<IDivDimension>;
    /**
     * Animation duration in milliseconds.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Edge of a parent element for one of the action types:where the element will move from when
     * appearing;where the element will move to when disappearing.
     */
    edge?: Type<DivSlideTransitionEdge | DivExpression>;
    /**
     * Transition speed nature.
     */
    interpolator?: Type<DivAnimationInterpolator | DivExpression>;
    /**
     * Delay in milliseconds before animation starts.
     */
    start_delay?: Type<number | DivExpression>;
}

export type DivSlideTransitionEdge =
    | 'left'
    | 'top'
    | 'right'
    | 'bottom';
