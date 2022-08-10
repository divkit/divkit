import { correctBorderRadius } from '../../src/utils/correctBorderRadius';

describe('correctBorderRadius', () => {
    test('simple', () => {
        expect(correctBorderRadius({
            'top-left': 1
        }, 0, 10, 'default')).toBe('0.1em 0 0 0');
        expect(correctBorderRadius({
            'top-left': -1
        }, 0, 10, 'default')).toBe('default');
        expect(correctBorderRadius({
            'top-left': 1,
            'top-right': 2
        }, 0, 10, 'default')).toBe('0.1em 0.2em 0 0');
        expect(correctBorderRadius({
            'top-left': 1,
            'top-right': 0
        }, 0, 10, 'default')).toBe('0.1em 0 0 0');
        expect(correctBorderRadius({
            'top-left': 1,
            'top-right': 2
        }, 4, 10, 'default')).toBe('0.1em 0.2em 0.4em 0.4em');
        expect(correctBorderRadius({
            'top-left': 1,
            'top-right': 2
        }, 4, 20, 'default')).toBe('0.05em 0.1em 0.2em 0.2em');
        expect(correctBorderRadius({
            'top-left': 1,
            'top-right': -2
        }, 0, 10, 'default')).toBe('default');
    });
});
