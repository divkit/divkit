// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivDownloadCallbacks,
} from './';

/**
 * It defines an action when clicking on an element.
 */
export interface IDivAction {
    /**
     * Callbacks that are called after [data loading](../../interaction.dita#loading-data).
     */
    download_callbacks?: Type<IDivDownloadCallbacks>;
    /**
     * Logging ID.
     */
    log_id: Type<string>;
    /**
     * URL for logging.
     */
    log_url?: Type<string | DivExpression>;
    /**
     * Context menu.
     */
    menu_items?: Type<NonEmptyArray<IDivActionMenuItem>>;
    /**
     * Additional parameters, passed to the host application.
     */
    payload?: Type<{}>;
    /**
     * Referer URL for logging.
     */
    referer?: Type<string | DivExpression>;
    /**
     * The tab in which the URL must be opened.
     */
    target?: Type<DivActionTarget | DivExpression>;
    /**
     * URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with
     * elements](../../interaction.dita).
     */
    url?: Type<string | DivExpression>;
}

export type DivActionTarget =
    | '_self'
    | '_blank';

export interface IDivActionMenuItem {
    /**
     * One action when clicking on a menu item. Not used if the `actions` parameter is set.
     */
    action?: Type<IDivAction>;
    /**
     * Multiple actions when clicking on a menu item.
     */
    actions?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Menu item title.
     */
    text: Type<string | DivExpression>;
}
