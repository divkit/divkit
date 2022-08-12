import type { EvalValue, NumberValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { NUMBER, STRING } from '../const';
import { parseColor, ParsedColor } from '../../utils/correctColor';
import { padLeft } from '../../utils/padLeft';

function safeConvertColor(color: string): ParsedColor {
    const res = parseColor(color);

    if (res) {
        return res;
    }

    throw new Error('Unable to convert value to Color, expected format #AARRGGBB.');
}

function stringifyColor(color: ParsedColor): string {
    return `#${[color.a, color.r, color.g, color.b].map(it => {
        if (it < 0 || it > 255) {
            throw new Error('Value out of range 0..1.');
        }

        return padLeft(Math.round(it).toString(16), 2);
    }).join('').toUpperCase()}`;
}

function colorGetter(field: keyof ParsedColor): (color: StringValue) => EvalValue {
    return color => {
        const parsed = safeConvertColor(color.value);

        return {
            type: NUMBER,
            value: parsed[field] / 255
        };
    };
}

function colorSetter(field: keyof ParsedColor): (color: StringValue, val: NumberValue) => EvalValue {
    return (color, val) => {
        const parsed = safeConvertColor(color.value);

        parsed[field] = val.value * 255;

        return {
            type: STRING,
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

function rgb(red: NumberValue, green: NumberValue, blue: NumberValue): EvalValue {
    const parsed: ParsedColor = {
        a: 255,
        r: red.value * 255,
        g: green.value * 255,
        b: blue.value * 255
    };

    return {
        type: STRING,
        value: stringifyColor(parsed)
    };
}

function argb(alpha: NumberValue, red: NumberValue, green: NumberValue, blue: NumberValue): EvalValue {
    const parsed: ParsedColor = {
        a: alpha.value * 255,
        r: red.value * 255,
        g: green.value * 255,
        b: blue.value * 255
    };

    return {
        type: STRING,
        value: stringifyColor(parsed)
    };
}

export function registerColors(): void {
    registerFunc('getColorAlpha', [STRING], getColorAlpha);
    registerFunc('getColorRed', [STRING], getColorRed);
    registerFunc('getColorGreen', [STRING], getColorGreen);
    registerFunc('getColorBlue', [STRING], getColorBlue);

    registerFunc('setColorAlpha', [STRING, NUMBER], setColorAlpha);
    registerFunc('setColorRed', [STRING, NUMBER], setColorRed);
    registerFunc('setColorGreen', [STRING, NUMBER], setColorGreen);
    registerFunc('setColorBlue', [STRING, NUMBER], setColorBlue);

    registerFunc('rgb', [NUMBER, NUMBER, NUMBER], rgb);
    registerFunc('argb', [NUMBER, NUMBER, NUMBER, NUMBER], argb);
}
