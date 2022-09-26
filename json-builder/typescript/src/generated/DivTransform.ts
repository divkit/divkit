// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivPivot,
} from './';

/**
 * Transformation of the element.
 */
export interface IDivTransform {
    /**
     * The X coordinate of the rotation axis.
     */
    pivot_x?: Type<DivPivot>;
    /**
     * The Y coordinate of the rotation axis.
     */
    pivot_y?: Type<DivPivot>;
    /**
     * The number of degrees by which the element must be rotated. A positive value describes a
     * clockwise rotation.
     */
    rotation?: Type<number | DivExpression>;
}
