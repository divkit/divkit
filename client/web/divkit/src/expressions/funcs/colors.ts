import type { ColorValue, EvalContext, EvalValue, NumberValue, StringValue } from '../eval';
import type { ParsedColor } from '../../utils/correctColor';
import { registerFunc } from './funcs';
import { COLOR, NUMBER, STRING } from '../const';
import { safeConvertColor, stringifyColor } from '../utils';

function colorGetter(
    field: keyof ParsedColor
): (_ctx: EvalContext, color: StringValue | ColorValue) => EvalValue {
    return (_vars, color) => {
        const parsed = safeConvertColor(color.value);

        return {
            type: NUMBER,
            value: parsed[field] / 255
        };
    };
}

function colorSetter(
    field: keyof ParsedColor
): (_ctx: EvalContext, color: StringValue | ColorValue, val: NumberValue) => EvalValue {
    return (_vars, color, val) => {
        const parsed = safeConvertColor(color.value);

        parsed[field] = val.value * 255;

        return {
            type: COLOR,
            value: stringifyColor(parsed)
        };
    };
}

const getColorAlpha = colorGetter('a');
const getColorRed = colorGetter('r');
const getColorGreen = colorGetter('g');
const getColorBlue = colorGetter('b');

const setColorAlpha = colorSetter('a');
const setColorRed = colorSetter('r');
const setColorGreen = colorSetter('g');
const setColorBlue = colorSetter('b');

function rgb(_ctx: EvalContext, red: NumberValue, green: NumberValue, blue: NumberValue): EvalValue {
    const parsed: ParsedColor = {
        a: 255,
        r: red.value * 255,
        g: green.value * 255,
        b: blue.value * 255
    };

    return {
        type: COLOR,
        value: stringifyColor(parsed)
    };
}

function argb(
    _ctx: EvalContext,
    alpha: NumberValue,
    red: NumberValue,
    green: NumberValue,
    blue: NumberValue
): EvalValue {
    const parsed: ParsedColor = {
        a: alpha.value * 255,
        r: red.value * 255,
        g: green.value * 255,
        b: blue.value * 255
    };

    return {
        type: COLOR,
        value: stringifyColor(parsed)
    };
}

function compositeAlpha(alpha0: number, alpha1: number): number {
    // mimic android & ios int calculation
    return 0xff - ((((0xff - alpha0) * (0xff - alpha1)) / 0xff) | 0);
}

function compositeComponent(
    bgColor: number,
    bgAlpha: number,
    fgColor: number,
    fgAlpha: number,
    alpha: number
): number {
    if (alpha === 0) {
        return 0;
    }

    return (
        (((0xFF * fgColor * fgAlpha) + (bgColor * bgAlpha * (0xFF - fgAlpha))) | 0) / (alpha * 0xFF)
    ) | 0;
}

function alphaBlend(_ctx: EvalContext, color0: ColorValue, color1: ColorValue): EvalValue {
    const parsed0 = safeConvertColor(color0.value);
    const parsed1 = safeConvertColor(color1.value);

    const totalAlpha = compositeAlpha(parsed0.a, parsed1.a);

    return {
        type: COLOR,
        value: stringifyColor({
            a: totalAlpha,
            r: compositeComponent(parsed0.r, parsed0.a, parsed1.r, parsed1.a, totalAlpha),
            g: compositeComponent(parsed0.g, parsed0.a, parsed1.g, parsed1.a, totalAlpha),
            b: compositeComponent(parsed0.b, parsed0.a, parsed1.b, parsed1.a, totalAlpha),
        })
    };
}

export function registerColors(): void {
    registerFunc('getColorAlpha', [STRING], getColorAlpha);
    registerFunc('getColorAlpha', [COLOR], getColorAlpha);
    registerFunc('getColorRed', [STRING], getColorRed);
    registerFunc('getColorRed', [COLOR], getColorRed);
    registerFunc('getColorGreen', [STRING], getColorGreen);
    registerFunc('getColorGreen', [COLOR], getColorGreen);
    registerFunc('getColorBlue', [STRING], getColorBlue);
    registerFunc('getColorBlue', [COLOR], getColorBlue);

    registerFunc('setColorAlpha', [STRING, NUMBER], setColorAlpha);
    registerFunc('setColorAlpha', [COLOR, NUMBER], setColorAlpha);
    registerFunc('setColorRed', [STRING, NUMBER], setColorRed);
    registerFunc('setColorRed', [COLOR, NUMBER], setColorRed);
    registerFunc('setColorGreen', [STRING, NUMBER], setColorGreen);
    registerFunc('setColorGreen', [COLOR, NUMBER], setColorGreen);
    registerFunc('setColorBlue', [STRING, NUMBER], setColorBlue);
    registerFunc('setColorBlue', [COLOR, NUMBER], setColorBlue);

    registerFunc('rgb', [NUMBER, NUMBER, NUMBER], rgb);
    registerFunc('argb', [NUMBER, NUMBER, NUMBER, NUMBER], argb);

    registerFunc('alphaBlend', [COLOR, COLOR], alphaBlend);
}
