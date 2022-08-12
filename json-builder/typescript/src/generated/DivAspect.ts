// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Размер с фиксированным соотношением сторон. Считает высоту от ширины и игнорирует другие
 * заданные значения высоты.
 */
export interface IDivAspect {
    /**
     * `ratio = width / height`.
     */
    ratio: Type<number> | DivExpression;
}
