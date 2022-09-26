// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivCornersRadius,
    IDivShadow,
    IDivStroke,
} from './';

/**
 * Stroke around the element.
 */
export interface IDivBorder {
    /**
     * One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
     */
    corner_radius?: Type<number | DivExpression>;
    /**
     * Multiple radii of element and stroke corner rounding.
     */
    corners_radius?: Type<IDivCornersRadius>;
    /**
     * Adding shadow.
     */
    has_shadow?: Type<IntBoolean | DivExpression>;
    /**
     * Shadow parameters.
     */
    shadow?: Type<IDivShadow>;
    /**
     * Stroke style.
     */
    stroke?: Type<IDivStroke>;
}
