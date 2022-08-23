// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivAction,
} from './';

/**
 * Callbacks that are called after [data loading](../../interaction.dita#loading-data).
 */
export interface IDivDownloadCallbacks {
    /**
     * Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
     */
    on_fail_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Actions in case of successful loading.
     */
    on_success_actions?: Type<NonEmptyArray<IDivAction>>;
}
