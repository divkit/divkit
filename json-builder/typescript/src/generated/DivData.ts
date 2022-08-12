// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
    DivTransitionSelector,
    DivVariable,
    IDivTrigger,
} from './';

/**
 * Корневая структура.
 */
export interface IDivData {
    /**
     * Идентификатор для логирования.
     */
    log_id: string;
    /**
     * Набор состояний визуальных элементов. Каждый элемент может иметь несколько состояний с разной
     * версткой. Состояния отображаются строго по одному, а переключаются с помощью
     * [action](div-action.md).
     */
    states: NonEmptyArray<IDivDataState>;
    /**
     * События, при которых сработают анимации переходов. Вместо него используйте
     * `transition_triggers`.
     *
     * @deprecated
     */
    transition_animation_selector?: DivTransitionSelector | DivExpression;
    /**
     * Триггеры изменения переменных.
     */
    variable_triggers?: NonEmptyArray<IDivTrigger>;
    /**
     * Объявление переменных, которые могут быть использованы в элементе.
     */
    variables?: NonEmptyArray<DivVariable>;
}

/**
 * Описания пока нет
 */
export interface IDivDataState {
    /**
     * Содержимое.
     */
    div: Div;
    /**
     * Идентификатор состояния.
     */
    state_id: number;
}

