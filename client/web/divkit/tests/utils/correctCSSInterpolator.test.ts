import { correctCSSInterpolator } from '../../src/utils/correctCSSInterpolator';

describe('correctCSSInterpolator', () => {
    test('simple', () => {
        expect(correctCSSInterpolator(undefined, 'ease_in_out')).toBe('ease_in_out');
        expect(correctCSSInterpolator('linear', 'ease')).toBe('linear');
        expect(correctCSSInterpolator('ease', 'linear')).toBe('ease');
        expect(correctCSSInterpolator('ease_in_out', 'ease')).toBe('ease_in_out');
        expect(correctCSSInterpolator('ease_in', 'ease')).toBe('ease_in');
        expect(correctCSSInterpolator('ease_out', 'ease')).toBe('ease_out');
        expect(correctCSSInterpolator('spring', 'ease')).toBe('ease');
    });
});
