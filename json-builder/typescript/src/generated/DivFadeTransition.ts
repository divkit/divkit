// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Анимация прозрачности.
 */
export class DivFadeTransition<T extends DivFadeTransitionProps = DivFadeTransitionProps> {
    readonly _props?: Exact<DivFadeTransitionProps, T>;

    readonly type = 'fade';
    /**
     * Значение альфа-канала, с которого начнется появление или на котором закончится исчезновение
     * элемента.
     */
    alpha?: Type<number> | DivExpression;
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

    constructor(props?: Exact<DivFadeTransitionProps, T>) {
        this.alpha = props?.alpha;
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

interface DivFadeTransitionProps {
    /**
     * Значение альфа-канала, с которого начнется появление или на котором закончится исчезновение
     * элемента.
     */
    alpha?: Type<number> | DivExpression;
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
