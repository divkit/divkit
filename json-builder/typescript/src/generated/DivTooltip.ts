// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    Div,
    IDivAnimation,
    IDivPoint,
} from './';

/**
 * Всплывающая подсказка.
 */
export interface IDivTooltip {
    /**
     * Анимация появления подсказки. По умолчанию подсказка будет появляться постепенно со смещением
     * от якорной точки на 10 dp.
     */
    animation_in?: Type<IDivAnimation>;
    /**
     * Анимация исчезания подсказки. По умолчанию подсказка будет исчезать постепенно со смещением от
     * якорной точки на 10 dp.
     */
    animation_out?: Type<IDivAnimation>;
    /**
     * Элемент, который будет показан в подсказке. Если внутри элемента есть подсказки, они не будут
     * показываться.
     */
    div: Type<Div>;
    /**
     * Продолжительность видимости подсказки в миллисекундах. При значении `0` подсказка будет видна
     * до тех пор, пока пользователь сам ее не скроет.
     */
    duration?: Type<number> | DivExpression;
    /**
     * Идентификатор подсказки. Используется, чтобы избежать повторного показа. Должен быть уникален
     * для всех подсказок элемента.
     */
    id: Type<string>;
    /**
     * Сдвиг относительно якорной точки.
     */
    offset?: Type<IDivPoint>;
    /**
     * Положение подсказки относительно элемента, к которому она относится.
     */
    position: Type<DivTooltipPosition> | DivExpression;
}

export type DivTooltipPosition =
    | 'left'
    | 'top-left'
    | 'top'
    | 'top-right'
    | 'right'
    | 'bottom-right'
    | 'bottom'
    | 'bottom-left';

