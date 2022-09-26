// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
    IDivAnimation,
    IDivPoint,
} from './';

/**
 * Tooltip.
 */
export interface IDivTooltip {
    /**
     * Tooltip appearance animation. By default, the tooltip will be appearing gradually with an
     * offset from the anchor point by 10 dp.
     */
    animation_in?: Type<IDivAnimation>;
    /**
     * Tooltip disappearance animation. By default, the tooltip will disappear gradually with an
     * offset from the anchor point by 10 dp.
     */
    animation_out?: Type<IDivAnimation>;
    /**
     * An element that will be shown in a tooltip. If there are tooltips inside an element, they
     * won't be shown.
     */
    div: Type<Div>;
    /**
     * Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip
     * will be visible until the user hides it.
     */
    duration?: Type<number | DivExpression>;
    /**
     * Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips.
     */
    id: Type<string>;
    /**
     * Shift relative to an anchor point.
     */
    offset?: Type<IDivPoint>;
    /**
     * The position of a tooltip relative to an element it belongs to.
     */
    position: Type<DivTooltipPosition | DivExpression>;
}

export type DivTooltipPosition =
    | 'left'
    | 'top-left'
    | 'top'
    | 'top-right'
    | 'right'
    | 'bottom-right'
    | 'bottom'
    | 'bottom-left';
