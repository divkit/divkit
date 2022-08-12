// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Анимация масштабирования.
 */
export class DivScaleTransition<T extends DivScaleTransitionProps = DivScaleTransitionProps> {
    readonly _props?: Exact<DivScaleTransitionProps, T>;

    readonly type = 'scale';
    /**
     * Продолжительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Характер скорости перехода.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Относительная координата `X` точки, которая не изменит своего положения при масштабировании.
     */
    pivot_x?: Type<number> | DivExpression;
    /**
     * Относительная координата `Y` точки, которая не изменит своего положения при масштабировании.
     */
    pivot_y?: Type<number> | DivExpression;
    /**
     * Значение масштаба, с которого начнется появление или на котором закончится исчезновение
     * элемента.
     */
    scale?: Type<number> | DivExpression;
    /**
     * Задержка в миллисекундах перед началом анимации.
     */
    start_delay?: Type<number> | DivExpression;

    constructor(props?: Exact<DivScaleTransitionProps, T>) {
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.pivot_x = props?.pivot_x;
        this.pivot_y = props?.pivot_y;
        this.scale = props?.scale;
        this.start_delay = props?.start_delay;
    }
}

interface DivScaleTransitionProps {
    /**
     * Продолжительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Характер скорости перехода.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Относительная координата `X` точки, которая не изменит своего положения при масштабировании.
     */
    pivot_x?: Type<number> | DivExpression;
    /**
     * Относительная координата `Y` точки, которая не изменит своего положения при масштабировании.
     */
    pivot_y?: Type<number> | DivExpression;
    /**
     * Значение масштаба, с которого начнется появление или на котором закончится исчезновение
     * элемента.
     */
    scale?: Type<number> | DivExpression;
    /**
     * Задержка в миллисекундах перед началом анимации.
     */
    start_delay?: Type<number> | DivExpression;
}
