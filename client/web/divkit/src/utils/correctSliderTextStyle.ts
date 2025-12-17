import type { SliderTextStyle } from '../types/slider';
import type { MaybeMissing } from '../expressions/json';
import type { TypefaceProvider } from '../../typings/common';
import { isPositiveNumber } from './isPositiveNumber';
import { correctColor } from './correctColor';
import { pxToEm } from './pxToEm';
import { correctFontWeight } from './correctFontWeight';
import { variationSettingsToString } from './variationSettings';

export interface TransformedSliderTextStyle {
    fontSize: string;
    fontWeight: number | undefined;
    fontFamily?: string;
    fontVariationSettings?: string;
    textColor: string;
    offset?: {
        x: number;
        y: number;
    };
}

export function correctSliderTextStyle(
    textStyle: MaybeMissing<SliderTextStyle> | undefined,
    typefaceProvider: TypefaceProvider,
    defaultValue: TransformedSliderTextStyle | undefined
): TransformedSliderTextStyle | undefined {
    if (!textStyle || !textStyle.font_size) {
        return defaultValue;
    }

    const offset = textStyle.offset;
    const convertedColor = textStyle.text_color && correctColor(textStyle.text_color) || '#000';
    const fontWeight = correctFontWeight(textStyle.font_weight, textStyle.font_weight_value, undefined);
    const fontVariationSettings = variationSettingsToString(textStyle.font_variation_settings) || undefined;

    if (
        isPositiveNumber(textStyle.font_size) &&
        convertedColor !== 'transparent'
    ) {
        const res: TransformedSliderTextStyle = {
            fontSize: pxToEm(textStyle.font_size),
            fontWeight,
            fontVariationSettings,
            textColor: convertedColor
        };

        if (typeof offset?.x?.value === 'number' && typeof offset?.y?.value === 'number') {
            res.offset = {
                x: offset.x.value,
                y: offset.y.value
            };
        }

        if (textStyle.font_family && typeof textStyle.font_family === 'string') {
            res.fontFamily = typefaceProvider(textStyle.font_family, {
                fontWeight
            }) || '';
        }

        return res;
    }
}
