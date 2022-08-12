// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivAction,
} from './';

/**
 * Колбэки, которые вызываются после [дозагрузки данных](../../interaction.dita#loading-data).
 */
export interface IDivDownloadCallbacks {
    /**
     * Действия при неуспешной загрузке, если о ней сообщил хост или закончилось время ожидания.
     */
    on_fail_actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Действия при успешной загрузке.
     */
    on_success_actions?: Type<NonEmptyArray<IDivAction>>;
}
