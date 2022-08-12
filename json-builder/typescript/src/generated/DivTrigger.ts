// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivAction,
} from './';

/**
 * Триггер, который вызывает действие при срабатывании.
 */
export interface IDivTrigger {
    /**
     * Действие при срабатывании триггера.
     */
    actions: Type<NonEmptyArray<IDivAction>>;
    /**
     * Условие для срабатывания триггера. Например, `liked && subscribed`.
     */
    condition: Type<IntBoolean> | DivExpression;
    /**
     * Режим запуска триггера:`on_condition` — триггер сработает при изменении условия с `false` на
     * `true`;`on_variable` — триггер сработает при выполнении условия и изменении значения
     * переменной.
     */
    mode?: Type<DivTriggerMode> | DivExpression;
}

export type DivTriggerMode =
    | 'on_condition'
    | 'on_variable';

