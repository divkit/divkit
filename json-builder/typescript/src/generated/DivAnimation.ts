// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
    DivCount,
} from './';

/**
 * Параметры анимации элементов.
 */
export interface IDivAnimation {
    /**
     * Длительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Конечное значение анимации.
     */
    end_value?: Type<number> | DivExpression;
    /**
     * Характер скорости анимации.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Элементы анимации.
     */
    items?: Type<NonEmptyArray<IDivAnimation>>;
    /**
     * Тип анимации.
     */
    name: Type<DivAnimationName> | DivExpression;
    /**
     * Число повторов анимации.
     */
    repeat?: Type<DivCount>;
    /**
     * Задержка в миллисекундах перед стартом анимации.
     */
    start_delay?: Type<number> | DivExpression;
    /**
     * Стартовое значение анимации.
     */
    start_value?: Type<number> | DivExpression;
}

export type DivAnimationName =
    | 'fade'
    | 'translate'
    | 'scale'
    | 'native'
    | 'set'
    | 'no_animation';

