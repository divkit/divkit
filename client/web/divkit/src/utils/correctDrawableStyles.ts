import type { Drawable } from '../types/drawable';
import type { Style } from '../types/general';
import type { MaybeMissing } from '../expressions/json';
import type { Circle, RoundedRectangle } from '../types/shape';
import type { Stroke } from '../types/border';
import { correctColor } from './correctColor';
import { pxToEm } from './pxToEm';
import { correctNonNegativeNumber } from './correctNonNegativeNumber';

export interface DrawableStyle extends Style {
    width: number;
    height: number;
    borderRadius: number;
    background: string;
    boxShadow?: string;
}

interface DrawableSize {
    width: number;
    height: number;
    radius: number;
}

function calcRoundedRectangleSize(shape: MaybeMissing<RoundedRectangle>): DrawableSize {
    return {
        width: correctNonNegativeNumber(shape.item_width?.value, 10),
        height: correctNonNegativeNumber(shape.item_height?.value, 10),
        radius: correctNonNegativeNumber(shape.corner_radius?.value, 5)
    };
}

function calcCircleSize(shape: MaybeMissing<Circle>): DrawableSize {
    const size = correctNonNegativeNumber(shape.radius?.value, 10) * 2;

    return {
        width: size,
        height: size,
        radius: size
    };
}

function calcDrawableStyle(
    size: DrawableSize,
    shape: MaybeMissing<RoundedRectangle | Circle>,
    old?: {
        stroke?: MaybeMissing<Stroke>;
        color?: string;
    }
): DrawableStyle {
    const stl: Partial<DrawableStyle> = {};

    const stroke = shape.stroke || old?.stroke;
    const borderColor = stroke?.color ? correctColor(stroke.color) : '';
    const borderWidth = stroke?.width ? Number(stroke.width ?? 1) : '';

    stl.width = size.width;
    stl.height = size.height;
    stl.borderRadius = size.radius;

    const bgColor = shape.background_color || old?.color;
    stl.background = correctColor(bgColor);

    if (borderColor && borderWidth) {
        stl.boxShadow = `inset 0 0 0 ${pxToEm(borderWidth)} ${borderColor}`;
    }

    return stl as DrawableStyle;
}

export function correctDrawableStyle<D = DrawableStyle>(
    drawable: MaybeMissing<Drawable> | undefined,
    availShapes: string[],
    defaultValue: D
): D {
    if (
        !drawable || !drawable.shape ||
        !drawable.shape.type || !availShapes.includes(drawable.shape.type) ||
        drawable.type !== 'shape_drawable'
    ) {
        return defaultValue;
    }
    let size: DrawableSize;
    if (drawable.shape.type === 'rounded_rectangle') {
        size = calcRoundedRectangleSize(drawable.shape);
    } else if (drawable.shape.type === 'circle') {
        size = calcCircleSize(drawable.shape);
    } else {
        return defaultValue;
    }

    return calcDrawableStyle(size, drawable.shape, {
        color: drawable.color,
        stroke: drawable.stroke
    }) as unknown as D;
}
