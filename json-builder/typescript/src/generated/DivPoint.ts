// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivDimension,
} from './';

/**
 * A point with fixed coordinates.
 */
export interface IDivPoint {
    /**
     * `X` coordinate.
     */
    x: Type<IDivDimension>;
    /**
     * `Y` coordinate.
     */
    y: Type<IDivDimension>;
}
