// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivAppearanceTransition,
} from './';

/**
 * A set of animations to be applied simultaneously.
 */
export class DivAppearanceSetTransition<T extends DivAppearanceSetTransitionProps = DivAppearanceSetTransitionProps> {
    readonly _props?: Exact<DivAppearanceSetTransitionProps, T>;

    readonly type = 'set';
    /**
     * An array of animations.
     */
    items: Type<NonEmptyArray<DivAppearanceTransition>>;

    constructor(props: Exact<DivAppearanceSetTransitionProps, T>) {
        this.items = props.items;
    }
}

export interface DivAppearanceSetTransitionProps {
    /**
     * An array of animations.
     */
    items: Type<NonEmptyArray<DivAppearanceTransition>>;
}
