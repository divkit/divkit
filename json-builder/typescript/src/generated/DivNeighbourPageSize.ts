// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivFixedSize,
} from './';

/**
 * Fixed width value of the visible part of a neighbouring page.
 */
export class DivNeighbourPageSize<T extends DivNeighbourPageSizeProps = DivNeighbourPageSizeProps> {
    readonly _props?: Exact<DivNeighbourPageSizeProps, T>;

    readonly type = 'fixed';
    /**
     * Width of the visible part of a neighbouring page.
     */
    neighbour_page_width: Type<DivFixedSize>;

    constructor(props: Exact<DivNeighbourPageSizeProps, T>) {
        this.neighbour_page_width = props.neighbour_page_width;
    }
}

export interface DivNeighbourPageSizeProps {
    /**
     * Width of the visible part of a neighbouring page.
     */
    neighbour_page_width: Type<DivFixedSize>;
}
