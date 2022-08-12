// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivAppearanceTransition,
} from './';

/**
 * Набор анимаций, которые будут применяться одновременно.
 */
export class DivAppearanceSetTransition<T extends DivAppearanceSetTransitionProps = DivAppearanceSetTransitionProps> {
    readonly _props?: Exact<DivAppearanceSetTransitionProps, T>;

    readonly type = 'set';
    /**
     * Массив анимаций.
     */
    items: Type<NonEmptyArray<DivAppearanceTransition>>;

    constructor(props: Exact<DivAppearanceSetTransitionProps, T>) {
        this.items = props.items;
    }
}

interface DivAppearanceSetTransitionProps {
    /**
     * Массив анимаций.
     */
    items: Type<NonEmptyArray<DivAppearanceTransition>>;
}
