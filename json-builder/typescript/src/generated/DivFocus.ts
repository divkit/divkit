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
 * Element behavior when focusing or losing focus.
 */
export interface IDivFocus {
    /**
     * Background of an element when it is in focus. It can contain multiple layers.
     */
    background?: Type<NonEmptyArray<DivBackground>>;
    /**
     * Border of an element when it is in focus
     */
    border?: Type<IDivBorder>;
    /**
     * IDs of elements that will be next to get focus.
     */
    next_focus_ids?: Type<IDivFocusNextFocusIds>;
    /**
     * Actions when an element loses focus.
     */
    on_blur?: Type<NonEmptyArray<IDivAction>>;
    /**
     * Actions when an element gets focus.
     */
    on_focus?: Type<NonEmptyArray<IDivAction>>;
}

/**
 * IDs of elements that will be next to get focus.
 */
export interface IDivFocusNextFocusIds {
    down?: Type<string | DivExpression>;
    forward?: Type<string | DivExpression>;
    left?: Type<string | DivExpression>;
    right?: Type<string | DivExpression>;
    up?: Type<string | DivExpression>;
}
