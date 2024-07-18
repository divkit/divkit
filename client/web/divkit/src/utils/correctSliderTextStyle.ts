import type { SliderTextStyle } from '../types/slider';
import type { MaybeMissing } from '../expressions/json';
import { isPositiveNumber } from './isPositiveNumber';
import { correctColor } from './correctColor';
import { pxToEm } from './pxToEm';
import { correctFontWeight } from './correctFontWeight';

export interface TransformedSliderTextStyle {
    fontSize: string;
    fontWeight: number | undefined;
    textColor: string;
    offset?: {
        x: number;
        y: number;
    };
}

export function correctSliderTextStyle(
    textStyle: MaybeMissing<SliderTextStyle> | undefined,
    defaultValue: TransformedSliderTextStyle | undefined
): TransformedSliderTextStyle | undefined {
    if (!textStyle || !textStyle.font_size) {
        return defaultValue;
    }

    const offset = textStyle.offset;
    const convertedColor = textStyle.text_color && correctColor(textStyle.text_color) || '#000';
    const fontWeight = correctFontWeight(textStyle.font_weight, textStyle.font_weight_value, undefined);

    if (
        isPositiveNumber(textStyle.font_size) &&
        convertedColor !== 'transparent'
    ) {
        const res: TransformedSliderTextStyle = {
            fontSize: pxToEm(textStyle.font_size),
            fontWeight,
            textColor: convertedColor
        };

        if (typeof offset?.x?.value === 'number' && typeof offset?.y?.value === 'number') {
            res.offset = {
                x: offset.x.value,
                y: offset.y.value
            };
        }

        return res;
    }
}
