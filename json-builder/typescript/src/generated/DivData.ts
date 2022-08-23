// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
    DivTransitionSelector,
    DivVariable,
    IDivTrigger,
} from './';

/**
 * Root structure.
 */
export interface IDivData {
    /**
     * Logging ID.
     */
    log_id: string;
    /**
     * A set of visual element states. Each element can have a few states with a different layout.
     * The states are displayed strictly one by one and switched using [action](div-action.md).
     */
    states: NonEmptyArray<IDivDataState>;
    /**
     * Events that trigger transition animations.
     *
     * @deprecated
     */
    transition_animation_selector?: DivTransitionSelector | DivExpression;
    /**
     * Triggers for changing variables.
     */
    variable_triggers?: NonEmptyArray<IDivTrigger>;
    /**
     * Declaration of variables that can be used in an element.
     */
    variables?: NonEmptyArray<DivVariable>;
}

export interface IDivDataState {
    /**
     * Contents.
     */
    div: Div;
    /**
     * State ID.
     */
    state_id: number;
}
