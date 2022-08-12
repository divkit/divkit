// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivPoint,
} from './';

/**
 * Тень элемента.
 */
export interface IDivShadow {
    /**
     * Прозрачность тени.
     */
    alpha?: Type<number> | DivExpression;
    /**
     * Интенсивность размытия.
     */
    blur?: Type<number> | DivExpression;
    /**
     * Цвет тени.
     */
    color?: Type<string> | DivExpression;
    /**
     * Смещение тени.
     */
    offset: Type<IDivPoint>;
}
