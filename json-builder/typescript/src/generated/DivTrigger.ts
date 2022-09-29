// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    IDivAction,
} from './';

/**
 * A trigger that causes an action when activated.
 */
export interface IDivTrigger {
    /**
     * Action when a trigger is activated.
     */
    actions: Type<NonEmptyArray<IDivAction>>;
    /**
     * Condition for activating a trigger. For example, `liked && subscribed`.
     */
    condition: Type<IntBoolean | DivExpression>;
    /**
     * Trigger activation mode:`on_condition` — a trigger is activated when the condition changes
     * from `false` to `true`;`on_variable` — a trigger is activated when the condition is met and
     * the variable value changes.
     */
    mode?: Type<DivTriggerMode | DivExpression>;
}

export type DivTriggerMode =
    | 'on_condition'
    | 'on_variable';
