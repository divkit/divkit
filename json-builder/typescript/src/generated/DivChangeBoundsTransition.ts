// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
} from './';

/**
 * Анимация изменения положения и размеров элемента.
 */
export class DivChangeBoundsTransition<T extends DivChangeBoundsTransitionProps = DivChangeBoundsTransitionProps> {
    readonly _props?: Exact<DivChangeBoundsTransitionProps, T>;

    readonly type = 'change_bounds';
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

    constructor(props?: Exact<DivChangeBoundsTransitionProps, T>) {
        this.duration = props?.duration;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

interface DivChangeBoundsTransitionProps {
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
