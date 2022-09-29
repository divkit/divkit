// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../safe-expression';

import {
    DivFixedSize,
} from './';

/**
 * A rectangle with rounded corners.
 */
export class DivRoundedRectangleShape<T extends DivRoundedRectangleShapeProps = DivRoundedRectangleShapeProps> {
    readonly _props?: Exact<DivRoundedRectangleShapeProps, T>;

    readonly type = 'rounded_rectangle';
    /**
     * Corner rounding radius.
     */
    corner_radius?: Type<DivFixedSize>;
    /**
     * Height.
     */
    item_height?: Type<DivFixedSize>;
    /**
     * Width.
     */
    item_width?: Type<DivFixedSize>;

    constructor(props?: Exact<DivRoundedRectangleShapeProps, T>) {
        this.corner_radius = props?.corner_radius;
        this.item_height = props?.item_height;
        this.item_width = props?.item_width;
    }
}

export interface DivRoundedRectangleShapeProps {
    /**
     * Corner rounding radius.
     */
    corner_radius?: Type<DivFixedSize>;
    /**
     * Height.
     */
    item_height?: Type<DivFixedSize>;
    /**
     * Width.
     */
    item_width?: Type<DivFixedSize>;
}
