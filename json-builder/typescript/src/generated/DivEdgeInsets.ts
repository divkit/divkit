// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * It sets margins.
 */
export interface IDivEdgeInsets {
    /**
     * Bottom margin.
     */
    bottom?: Type<number> | DivExpression;
    /**
     * Left margin.
     */
    left?: Type<number> | DivExpression;
    /**
     * Right margin.
     */
    right?: Type<number> | DivExpression;
    /**
     * Top margin.
     */
    top?: Type<number> | DivExpression;
    unit?: Type<DivSizeUnit> | DivExpression;
}
