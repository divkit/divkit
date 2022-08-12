// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Значение измерения элемента.
 */
export interface IDivDimension {
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Значение.
     */
    value: Type<number> | DivExpression;
}
