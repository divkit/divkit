// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

import {
    DivShape,
    IDivStroke,
} from './';

/**
 * Drawable простой геометрической формы.
 */
export class DivShapeDrawable<T extends DivShapeDrawableProps = DivShapeDrawableProps> {
    readonly _props?: Exact<DivShapeDrawableProps, T>;

    readonly type = 'shape_drawable';
    /**
     * Цвет заливки.
     */
    color: Type<string> | DivExpression;
    /**
     * Форма.
     */
    shape: Type<DivShape>;
    /**
     * Стиль контура.
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
     * Цвет заливки.
     */
    color: Type<string> | DivExpression;
    /**
     * Форма.
     */
    shape: Type<DivShape>;
    /**
     * Стиль контура.
     */
    stroke?: Type<IDivStroke>;
}
