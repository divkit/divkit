import type { Drawable } from '../types/drawable';
import type { Style } from '../types/general';
import type { MaybeMissing } from '../expressions/json';
import { correctColor } from './correctColor';
import { pxToEm } from './pxToEm';

export interface DrawableStyle extends Style {
    width: number;
    height: number;
    borderRadius: number;
    background: string;
    boxShadow?: string;
}

export function correctDrawableStyle<D = DrawableStyle>(
    drawable: MaybeMissing<Drawable> | undefined,
    availShapes: string[],
    defaultValue: D
): D {
    if (
        !drawable || !drawable.shape || !drawable.color ||
        !drawable.shape.type || !availShapes.includes(drawable.shape.type)
    ) {
        return defaultValue;
    }

    const bg = correctColor(drawable.color);
    let width: number;
    let height: number;
    let radius: number;
    if (drawable.shape.type === 'rounded_rectangle') {
        width = Number(drawable.shape.item_width?.value || 10);
        height = Number(drawable.shape.item_height?.value || 10);
        radius = Number(drawable.shape.corner_radius?.value ?? 5);
    } else if (drawable.shape.type === 'circle') {
        width = height = radius = Number(drawable.shape.radius?.value || 10) * 2;
    } else {
        return defaultValue;
    }

    if (
        drawable.type === 'shape_drawable' &&
        bg !== 'transparent'
    ) {
        const stl: Partial<DrawableStyle> = {};

        const borderColor = drawable.stroke?.color ? correctColor(drawable.stroke.color) : '';
        const borderWidth = drawable.stroke?.width ? Number(drawable.stroke.width ?? 1) : '';

        stl.width = width;
        stl.height = height;
        stl.borderRadius = radius;
        stl.background = bg;

        if (borderColor && borderWidth) {
            stl.boxShadow = `inset 0 0 0 ${pxToEm(borderWidth)} ${borderColor}`;
        }

        return stl as D;
    }

    return defaultValue;
}
