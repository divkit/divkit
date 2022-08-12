// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivDimension,
} from './';

/**
 * Точка с фиксированными координатами.
 */
export interface IDivPoint {
    /**
     * Координата `X`.
     */
    x: Type<IDivDimension>;
    /**
     * Координата `Y`.
     */
    y: Type<IDivDimension>;
}
