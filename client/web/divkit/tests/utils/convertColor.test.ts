import {
    describe,
    expect,
    test
} from 'vitest';

import { correctColor } from '../../src/utils/correctColor';

describe('correctColor', () => {
    test('simple', () => {
        expect(correctColor('#fc0')).toBe('#ffcc00');
        expect(correctColor('#afc0')).toBe('rgba(255,204,0,0.67)');
        expect(correctColor('#ffcc00')).toBe('#ffcc00');
        expect(correctColor('#aaffcc00')).toBe('rgba(255,204,0,0.67)');
    });

    test('with alpha', () => {
        expect(correctColor('#fc0', 0.5)).toBe('rgba(255,204,0,0.50)');
        expect(correctColor('#afc0', 0.5)).toBe('rgba(255,204,0,0.33)');
        expect(correctColor('#ffcc00', 0.5)).toBe('rgba(255,204,0,0.50)');
        expect(correctColor('#aaffcc00', 0.5)).toBe('rgba(255,204,0,0.33)');
    });

    test('incorrect', () => {
        expect(correctColor('red')).toBe('transparent');
        expect(correctColor('#12345')).toBe('transparent');
        expect(correctColor('rgba(255,204,0,0.67)')).toBe('transparent');
    });
});
