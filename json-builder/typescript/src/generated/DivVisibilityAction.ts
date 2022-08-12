// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivDownloadCallbacks,
} from './';

/**
 * Действия, которые выполняются, когда элемент становится видимым.
 */
export interface IDivVisibilityAction {
    /**
     * Колбэки, которые вызываются после [дозагрузки данных](../../interaction.dita#loading-data).
     */
    download_callbacks?: Type<IDivDownloadCallbacks>;
    /**
     * Идентификатор для логирования.
     */
    log_id: Type<string>;
    /**
     * Ограничение на количество логирований. При `0` ограничение снимается.
     */
    log_limit?: Type<number> | DivExpression;
    /**
     * Дополнительные параметры, передаются приложению-хосту.
     */
    payload?: Type<{}>;
    /**
     * Referer-ссылка для логирования.
     */
    referer?: Type<string> | DivExpression;
    /**
     * Ссылка. Возможные значения: `url` или `div-action://`. Подробнее в разделе [Взаимодействие с
     * элементами](../../interaction.dita).
     */
    url?: Type<string> | DivExpression;
    /**
     * Время в миллисекундах, в течение которого элемент должен быть виден, чтобы сработал
     * `visibility-action`.
     */
    visibility_duration?: Type<number> | DivExpression;
    /**
     * Процент видимой части элемента, при котором срабатывает `visibility-action`.
     */
    visibility_percentage?: Type<number> | DivExpression;
}
