// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivChangeTransition,
} from './';

/**
 * Animations.
 */
export class DivChangeSetTransition<T extends DivChangeSetTransitionProps = DivChangeSetTransitionProps> {
    readonly _props?: Exact<DivChangeSetTransitionProps, T>;

    readonly type = 'set';
    /**
     * List of animations.
     */
    items: Type<NonEmptyArray<DivChangeTransition>>;

    constructor(props: Exact<DivChangeSetTransitionProps, T>) {
        this.items = props.items;
    }
}

export interface DivChangeSetTransitionProps {
    /**
     * List of animations.
     */
    items: Type<NonEmptyArray<DivChangeTransition>>;
}
