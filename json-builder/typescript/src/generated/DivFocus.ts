// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivBackground,
    IDivAction,
    IDivBorder,
} from './';

/**
 * Поведение элемента при фокусировке или потере фокуса.
 */
export interface IDivFocus {
    /**
     * Фон элемента, когда он в фокусе. Может содержать несколько слоев.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Рамка элемента, когда он в фокусе.
     */
    border?: Type<IDivBorder>;
    /**
     * Идентификаторы элементов, которые следующими получат фокус.
     */
    next_focus_ids?: Type<IDivFocusNextFocusIds>;
    /**
     * Действия, когда элемент теряет фокус.
     */
    on_blur?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Действия, когда элемент получает фокус.
     */
    on_focus?: Type<NonEmptyArray<IDivAction>>;
}

/**
 * Идентификаторы элементов, которые следующими получат фокус.
 */
export interface IDivFocusNextFocusIds {
    down?: Type<string> | DivExpression;
    forward?: Type<string> | DivExpression;
    left?: Type<string> | DivExpression;
    right?: Type<string> | DivExpression;
    up?: Type<string> | DivExpression;
}

