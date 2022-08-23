// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Stroke.
 */
export interface IDivStroke {
    /**
     * Stroke color.
     */
    color: Type<string> | DivExpression;
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Stroke width.
     */
    width?: Type<number> | DivExpression;
}
