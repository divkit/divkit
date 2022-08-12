// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAnimationInterpolator,
    IDivDimension,
} from './';

/**
 * Анимация перемещения.
 */
export class DivSlideTransition<T extends DivSlideTransitionProps = DivSlideTransitionProps> {
    readonly _props?: Exact<DivSlideTransitionProps, T>;

    readonly type = 'slide';
    /**
     * Фиксированное значение смещения, с которого начнется появление или на котором закончится
     * исчезновение элемента. Если значение не указано, то берется расстояние до выбранного края
     * родительского элемента.
     */
    distance?: Type<IDivDimension>;
    /**
     * Продолжительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Край родительского элемента для одного из типов действий:откуда элемент будет двигаться при
     * появлении;куда элемент будет двигаться при исчезновении.
     */
    edge?: Type<DivSlideTransitionEdge> | DivExpression;
    /**
     * Характер скорости перехода.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Задержка в миллисекундах перед началом анимации.
     */
    start_delay?: Type<number> | DivExpression;

    constructor(props?: Exact<DivSlideTransitionProps, T>) {
        this.distance = props?.distance;
        this.duration = props?.duration;
        this.edge = props?.edge;
        this.interpolator = props?.interpolator;
        this.start_delay = props?.start_delay;
    }
}

interface DivSlideTransitionProps {
    /**
     * Фиксированное значение смещения, с которого начнется появление или на котором закончится
     * исчезновение элемента. Если значение не указано, то берется расстояние до выбранного края
     * родительского элемента.
     */
    distance?: Type<IDivDimension>;
    /**
     * Продолжительность анимации в миллисекундах.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Край родительского элемента для одного из типов действий:откуда элемент будет двигаться при
     * появлении;куда элемент будет двигаться при исчезновении.
     */
    edge?: Type<DivSlideTransitionEdge> | DivExpression;
    /**
     * Характер скорости перехода.
     */
    interpolator?: Type<DivAnimationInterpolator> | DivExpression;
    /**
     * Задержка в миллисекундах перед началом анимации.
     */
    start_delay?: Type<number> | DivExpression;
}

export type DivSlideTransitionEdge =
    | 'left'
    | 'top'
    | 'right'
    | 'bottom';

