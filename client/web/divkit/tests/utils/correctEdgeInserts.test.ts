import { correctEdgeInserts } from '../../src/utils/correctEdgeInserts';

describe('correctEdgeInserts', () => {
    test('simple', () => {
        expect(correctEdgeInserts(undefined, 'default')).toBe('default');
        expect(correctEdgeInserts({
            top: 1
        }, 'default')).toBe('0.1em 0 0 0');
        expect(correctEdgeInserts({
            top: 1,
            bottom: 1
        }, 'default')).toBe('0.1em 0 0.1em 0');
        expect(correctEdgeInserts({
            top: 1,
            bottom: -1
        }, 'default')).toBe('default');
        expect(correctEdgeInserts({
            top: 1,
            bottom: 0
        }, 'default')).toBe('0.1em 0 0 0');
    });
});
