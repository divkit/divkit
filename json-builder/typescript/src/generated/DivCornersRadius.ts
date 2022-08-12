// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Устанавливает скругление углов.
 */
export interface IDivCornersRadius {
    /**
     * Радиус скругления нижнего левого угла. Если не указано, то используется `corner_radius`.
     */
    'bottom-left'?: Type<number> | DivExpression;
    /**
     * Радиус скругления нижнего правого угла. Если не указано, то используется `corner_radius`.
     */
    'bottom-right'?: Type<number> | DivExpression;
    /**
     * Радиус скругления верхнего левого угла. Если не указано, то используется `corner_radius`.
     */
    'top-left'?: Type<number> | DivExpression;
    /**
     * Радиус скругления верхнего правого угла. Если не указано, то используется `corner_radius`.
     */
    'top-right'?: Type<number> | DivExpression;
}
