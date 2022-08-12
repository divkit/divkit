// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivChangeTransition,
} from './';

/**
 * Анимации.
 */
export class DivChangeSetTransition<T extends DivChangeSetTransitionProps = DivChangeSetTransitionProps> {
    readonly _props?: Exact<DivChangeSetTransitionProps, T>;

    readonly type = 'set';
    /**
     * Список анимаций.
     */
    items: Type<NonEmptyArray<DivChangeTransition>>;

    constructor(props: Exact<DivChangeSetTransitionProps, T>) {
        this.items = props.items;
    }
}

interface DivChangeSetTransitionProps {
    /**
     * Список анимаций.
     */
    items: Type<NonEmptyArray<DivChangeTransition>>;
}
