import type { Drawable } from '../types/slider';
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

export function correctDrawableStyle(
    drawable: MaybeMissing<Drawable> | undefined,
    defaultValue: DrawableStyle
): DrawableStyle {
    if (!drawable || !drawable.shape || !drawable.color) {
        return defaultValue;
    }

    const bg = correctColor(drawable.color);
    const width = Number(drawable.shape.item_width?.value || 10);
    const height = Number(drawable.shape.item_height?.value || 10);
    const radius = Number(drawable.shape.corner_radius?.value || 5);

    if (
        drawable.type === 'shape_drawable' &&
        bg !== 'transparent' &&
        drawable.shape.type === 'rounded_rectangle'
    ) {
        const stl: Partial<DrawableStyle> = {};

        const borderColor = drawable.stroke?.color ? correctColor(drawable.stroke.color) : '';
        const borderWidth = drawable.stroke?.width ? Number(drawable.stroke.width || 1) : '';

        stl.width = width;
        stl.height = height;
        stl.borderRadius = radius;
        stl.background = bg;

        if (borderColor && borderWidth) {
            stl.boxShadow = `inset 0 0 0 ${pxToEm(borderWidth)} ${borderColor}`;
        }

        return stl as DrawableStyle;
    }

    return defaultValue;
}
