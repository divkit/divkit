// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivFixedSize,
} from './';

/**
 * Фиксированное значение ширины видимой части соседней страницы.
 */
export class DivNeighbourPageSize<T extends DivNeighbourPageSizeProps = DivNeighbourPageSizeProps> {
    readonly _props?: Exact<DivNeighbourPageSizeProps, T>;

    readonly type = 'fixed';
    /**
     * Ширина видимой части соседней страницы.
     */
    neighbour_page_width: Type<DivFixedSize>;

    constructor(props: Exact<DivNeighbourPageSizeProps, T>) {
        this.neighbour_page_width = props.neighbour_page_width;
    }
}

interface DivNeighbourPageSizeProps {
    /**
     * Ширина видимой части соседней страницы.
     */
    neighbour_page_width: Type<DivFixedSize>;
}
