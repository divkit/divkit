import { correctTintMode } from '../../src/utils/correctTintMode';

describe('correctTintMode', () => {
    test('simple', () => {
        expect(correctTintMode(undefined, 'source_atop')).toBe('source_atop');
        expect(correctTintMode('source_in', 'source_atop')).toBe('source_in');
        expect(correctTintMode('source_atop', 'source_in')).toBe('source_atop');
        expect(correctTintMode('darken', 'lighten')).toBe('darken');
        expect(correctTintMode('lighten', 'darken')).toBe('lighten');
        expect(correctTintMode('multiply', 'screen')).toBe('multiply');
        expect(correctTintMode('screen', 'multiply')).toBe('screen');
    });
});
