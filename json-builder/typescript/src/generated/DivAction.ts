// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivHover,
    IDivDownloadCallbacks,
} from './';

/**
 * Определяет действие при нажатии на элемент.
 */
export interface IDivAction {
    /**
     * Колбэки, которые вызываются после [дозагрузки данных](../../interaction.dita#loading-data).
     */
    download_callbacks?: Type<IDivDownloadCallbacks>;
    /**
     * Действие при наведении на элемент.
     *
     * @deprecated
     */
    hover?: Type<DivHover>;
    /**
     * Идентификатор для логирования.
     */
    log_id: Type<string>;
    /**
     * Ссылка для логирования.
     */
    log_url?: Type<string> | DivExpression;
    /**
     * Контекстное меню.
     */
    menu_items?: Type<NonEmptyArray<IDivActionMenuItem>>;
    /**
     * Дополнительные параметры, передаются приложению-хосту.
     */
    payload?: Type<{}>;
    /**
     * Referer-ссылка для логирования.
     */
    referer?: Type<string> | DivExpression;
    /**
     * Вкладка, в которой должна открыться ссылка.
     */
    target?: Type<DivActionTarget> | DivExpression;
    /**
     * Ссылка. Возможные значения: `url` или `div-action://`. Подробнее в разделе [Взаимодействие с
     * элементами](../../interaction.dita).
     */
    url?: Type<string> | DivExpression;
}

export type DivActionTarget =
    | '_self'
    | '_blank';

/**
 * Описания пока нет
 */
export interface IDivActionMenuItem {
    /**
     * Одно действие при нажатии на пункт меню. Не используется, если задан параметр `actions`.
     */
    action?: Type<IDivAction>;
    /**
     * Несколько действий при нажатии на пункт меню.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Заголовок пункта меню.
     */
    text: Type<string> | DivExpression;
}

