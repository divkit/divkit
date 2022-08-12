// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    IDivCornersRadius,
    IDivShadow,
    IDivStroke,
} from './';

/**
 * Рамка вокруг элемента.
 */
export interface IDivBorder {
    /**
     * Один радиус скругления углов элемента и рамки. Имеет более низкий приоритет, чем
     * `corners_radius`.
     */
    corner_radius?: Type<number> | DivExpression;
    /**
     * Несколько радиусов скругления углов элемента и рамки.
     */
    corners_radius?: Type<IDivCornersRadius>;
    /**
     * Добавление тени.
     */
    has_shadow?: Type<IntBoolean> | DivExpression;
    /**
     * Параметры тени.
     */
    shadow?: Type<IDivShadow>;
    /**
     * Стиль рамки.
     */
    stroke?: Type<IDivStroke>;
}
