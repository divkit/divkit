// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivShape,
    IDivStroke,
} from './';

/**
 * Drawable of a simple geometric shape.
 */
export class DivShapeDrawable<T extends DivShapeDrawableProps = DivShapeDrawableProps> {
    readonly _props?: Exact<DivShapeDrawableProps, T>;

    readonly type = 'shape_drawable';
    /**
     * Fill color.
     */
    color: Type<string> | DivExpression;
    /**
     * Shape.
     */
    shape: Type<DivShape>;
    /**
     * Stroke style.
     */
    stroke?: Type<IDivStroke>;

    constructor(props: Exact<DivShapeDrawableProps, T>) {
        this.color = props.color;
        this.shape = props.shape;
        this.stroke = props.stroke;
    }
}

interface DivShapeDrawableProps {
    /**
     * Fill color.
     */
    color: Type<string> | DivExpression;
    /**
     * Shape.
     */
    shape: Type<DivShape>;
    /**
     * Stroke style.
     */
    stroke?: Type<IDivStroke>;
}
