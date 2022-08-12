// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Описания пока нет
 */
export interface IDivTransitionBase {
    /**
     * Продолжительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Характер скорости перехода.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Задержка в миллисекундах перед началом анимации.
     */
    start_delay?: Type<number> | DivExpression;
}
