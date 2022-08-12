// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivFixedSize,
} from './';

/**
 * Прямоугольник со скругленными углами.
 */
export class DivRoundedRectangleShape<T extends DivRoundedRectangleShapeProps = DivRoundedRectangleShapeProps> {
    readonly _props?: Exact<DivRoundedRectangleShapeProps, T>;

    readonly type = 'rounded_rectangle';
    /**
     * Радиус скругления углов.
     */
    corner_radius?: Type<DivFixedSize>;
    /**
     * Высота.
     */
    item_height?: Type<DivFixedSize>;
    /**
     * Ширина.
     */
    item_width?: Type<DivFixedSize>;

    constructor(props?: Exact<DivRoundedRectangleShapeProps, T>) {
        this.corner_radius = props?.corner_radius;
        this.item_height = props?.item_height;
        this.item_width = props?.item_width;
    }
}

interface DivRoundedRectangleShapeProps {
    /**
     * Радиус скругления углов.
     */
    corner_radius?: Type<DivFixedSize>;
    /**
     * Высота.
     */
    item_height?: Type<DivFixedSize>;
    /**
     * Ширина.
     */
    item_width?: Type<DivFixedSize>;
}
