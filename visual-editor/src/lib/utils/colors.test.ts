import { describe, test, expect } from 'vitest';
import { stringifyColorToCss, divkitColorToCss, stringifyColorToDivKit, divkitColorToCssWithoutAlpha, parseColor, hsvToRgb, rgbToHsv } from './colors';

describe('colors', () => {
    test('stringifyColorToCss', () => {
        expect(stringifyColorToCss()).toEqual('transparent');
        expect(stringifyColorToCss({
            a: 255,
            r: 255,
            g: 255,
            b: 255
        })).toEqual('#ffffff');
        expect(stringifyColorToCss({
            a: 100,
            r: 255,
            g: 255,
            b: 255
        })).toEqual('rgba(255,255,255,0.39)');
    });

    test('stringifyColorToDivKit', () => {
        expect(stringifyColorToDivKit({
            a: 255,
            r: 255,
            g: 255,
            b: 255
        })).toEqual('#ffffff');
        expect(stringifyColorToDivKit({
            a: 100,
            r: 255,
            g: 255,
            b: 255
        })).toEqual('#64ffffff');
    });

    test('divkitColorToCss', () => {
        expect(divkitColorToCss('')).toEqual('');
        expect(divkitColorToCss('#fff')).toEqual('#ffffff');
        expect(divkitColorToCss('#ffff')).toEqual('#ffffff');
        expect(divkitColorToCss('#ffffff')).toEqual('#ffffff');
        expect(divkitColorToCss('#ffffffff')).toEqual('#ffffff');
        expect(divkitColorToCss('#64ffffff')).toEqual('rgba(255,255,255,0.39)');
    });

    test('divkitColorToCssWithoutAlpha', () => {
        expect(divkitColorToCssWithoutAlpha('')).toEqual('transparent');
        expect(divkitColorToCssWithoutAlpha('#fff')).toEqual('#ffffff');
        expect(divkitColorToCssWithoutAlpha('#ffff')).toEqual('#ffffff');
        expect(divkitColorToCssWithoutAlpha('#ffffff')).toEqual('#ffffff');
        expect(divkitColorToCssWithoutAlpha('#ffffffff')).toEqual('#ffffff');
        expect(divkitColorToCssWithoutAlpha('#64ffffff')).toEqual('#ffffff');
    });

    test('parseColor', () => {
        expect(parseColor('#fff')).toEqual({
            a: 255,
            r: 255,
            g: 255,
            b: 255
        });
        expect(parseColor('#64ffffff')).toEqual({
            a: 100,
            r: 255,
            g: 255,
            b: 255
        });
    });

    test('hsvToRgb', () => {
        expect(hsvToRgb({
            a: 1,
            h: 0,
            s: 1,
            v: 1
        })).toEqual({
            a: 255,
            r: 255,
            g: 0,
            b: 0
        });

        expect(hsvToRgb({
            a: 1,
            h: .5,
            s: 1,
            v: 1
        })).toEqual({
            a: 255,
            r: 0,
            g: 255,
            b: 255
        });
    });

    test('rgbToHsv', () => {
        expect(rgbToHsv({
            a: 255,
            r: 255,
            g: 0,
            b: 0
        })).toEqual({
            a: 1,
            h: 0,
            s: 1,
            v: 1
        });

        expect(rgbToHsv({
            a: 255,
            r: 0,
            g: 255,
            b: 255
        })).toEqual({
            a: 1,
            h: .5,
            s: 1,
            v: 1
        });
    });
});
