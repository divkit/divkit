// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Рамка.
 */
export interface IDivStroke {
    /**
     * Цвет рамки.
     */
    color: Type<string> | DivExpression;
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Толщина рамки.
     */
    width?: Type<number> | DivExpression;
}
