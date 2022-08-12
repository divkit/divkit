// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivSizeUnit,
} from './';

/**
 * Фиксированный размер элемента.
 */
export class DivFixedSize<T extends DivFixedSizeProps = DivFixedSizeProps> {
    readonly _props?: Exact<DivFixedSizeProps, T>;

    readonly type = 'fixed';
    /**
     * Единица измерения. Подробнее о единицах измерения размера в разделе [Верстка внутри
     * карточки](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Размер элемента.
     */
    value: Type<number> | DivExpression;

    constructor(props: Exact<DivFixedSizeProps, T>) {
        this.unit = props.unit;
        this.value = props.value;
    }
}

interface DivFixedSizeProps {
    /**
     * Единица измерения. Подробнее о единицах измерения размера в разделе [Верстка внутри
     * карточки](../../layout.dita).
     */
    unit?: Type<DivSizeUnit> | DivExpression;
    /**
     * Размер элемента.
     */
    value: Type<number> | DivExpression;
}
