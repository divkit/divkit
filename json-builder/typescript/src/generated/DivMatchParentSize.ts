// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Размер элемента подстраивается под родительский элемент.
 */
export class DivMatchParentSize<T extends DivMatchParentSizeProps = DivMatchParentSizeProps> {
    readonly _props?: Exact<DivMatchParentSizeProps, T>;

    readonly type = 'match_parent';
    /**
     * Вес при распределении свободного места между элементами с типом размера `match_parent` внутри
     * элемента. Если вес не указан, то элементы поделят место поровну.
     */
    weight?: Type<number> | DivExpression;

    constructor(props?: Exact<DivMatchParentSizeProps, T>) {
        this.weight = props?.weight;
    }
}

interface DivMatchParentSizeProps {
    /**
     * Вес при распределении свободного места между элементами с типом размера `match_parent` внутри
     * элемента. Если вес не указан, то элементы поделят место поровну.
     */
    weight?: Type<number> | DivExpression;
}
