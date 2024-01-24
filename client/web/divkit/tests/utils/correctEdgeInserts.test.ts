import { correctEdgeInserts } from '../../src/utils/correctEdgeInserts';

describe('correctEdgeInserts', () => {
    test('simple', () => {
        expect(correctEdgeInserts(undefined, 'ltr', 'default')).toBe('default');
        expect(correctEdgeInserts({
            top: 1
        }, 'ltr', 'default')).toBe('0.1em 0 0 0');
        expect(correctEdgeInserts({
            top: 1,
            bottom: 1
        }, 'ltr', 'default')).toBe('0.1em 0 0.1em 0');
        expect(correctEdgeInserts({
            top: 1,
            bottom: -1
        }, 'ltr', 'default')).toBe('default');
        expect(correctEdgeInserts({
            top: 1,
            bottom: 0
        }, 'ltr', 'default')).toBe('0.1em 0 0 0');
    });

    test('ltr', () => {
        expect(correctEdgeInserts({
            top: 1,
            right: 2
        }, 'ltr', 'default')).toBe('0.1em 0.2em 0 0');
        expect(correctEdgeInserts({
            top: 1,
            right: 2,
            end: 3
        }, 'ltr', 'default')).toBe('0.1em 0.3em 0 0');
    });

    test('rtl', () => {
        expect(correctEdgeInserts({
            top: 1,
            right: 2
        }, 'rtl', 'default')).toBe('0.1em 0.2em 0 0');
        expect(correctEdgeInserts({
            top: 1,
            right: 2,
            start: 3
        }, 'rtl', 'default')).toBe('0.1em 0.3em 0 0');
    });
});
