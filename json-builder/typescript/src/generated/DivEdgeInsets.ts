// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Устанавливает отступы.
 */
export interface IDivEdgeInsets {
    /**
     * Нижний отступ.
     */
    bottom?: Type<number> | DivExpression;
    /**
     * Левый отступ.
     */
    left?: Type<number> | DivExpression;
    /**
     * Правый отступ.
     */
    right?: Type<number> | DivExpression;
    /**
     * Верхний отступ.
     */
    top?: Type<number> | DivExpression;
    unit?: Type<DivSizeUnit> | DivExpression;
}
