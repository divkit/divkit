// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivDownloadCallbacks,
} from './';

/**
 * Actions performed when an element becomes visible.
 */
export interface IDivVisibilityAction {
    /**
     * Callbacks that are called after [data loading](../../interaction.dita#loading-data).
     */
    download_callbacks?: Type<IDivDownloadCallbacks>;
    /**
     * Logging ID.
     */
    log_id: Type<string>;
    /**
     * Limit on the number of loggings. If `0`, the limit is removed.
     */
    log_limit?: Type<number> | DivExpression;
    /**
     * Additional parameters, passed to the host application.
     */
    payload?: Type<{}>;
    /**
     * Referer URL for logging.
     */
    referer?: Type<string> | DivExpression;
    /**
     * URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with
     * elements](../../interaction.dita).
     */
    url?: Type<string> | DivExpression;
    /**
     * Time in milliseconds during which an element must be visible to trigger `visibility-action`.
     */
    visibility_duration?: Type<number> | DivExpression;
    /**
     * Percentage of the visible part of an element that triggers `visibility-action`.
     */
    visibility_percentage?: Type<number> | DivExpression;
}
