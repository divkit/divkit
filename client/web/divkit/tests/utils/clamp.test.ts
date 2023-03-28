import { clamp } from '../../src/utils/clamp';

describe('clamp', () => {
    test('simple', () => {
        expect(clamp(2, 1, 3)).toBe(2);
        expect(clamp(4, 1, 3)).toBe(3);
        expect(clamp(0, 1, 3)).toBe(1);
    });

    test('bigint', () => {
        expect(clamp(BigInt(2), 1, 3)).toBe(2);
        expect(clamp(BigInt(4), 1, 3)).toBe(3);
        expect(clamp(BigInt(0), 1, 3)).toBe(1);
    });

    test('unexpected', () => {
        expect(clamp(2, 3, 3)).toBe(3);
        expect(clamp(3, 3, 3)).toBe(3);
        expect(clamp(2, 3, 1)).toBe(3);
    });
});
