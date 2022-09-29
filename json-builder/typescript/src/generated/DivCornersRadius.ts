// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

/**
 * Sets corner rounding.
 */
export interface IDivCornersRadius {
    /**
     * Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
     */
    'bottom-left'?: Type<number | DivExpression>;
    /**
     * Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
     */
    'bottom-right'?: Type<number | DivExpression>;
    /**
     * Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
     */
    'top-left'?: Type<number | DivExpression>;
    /**
     * Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
     */
    'top-right'?: Type<number | DivExpression>;
}
