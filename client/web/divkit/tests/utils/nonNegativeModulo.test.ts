import {
    describe,
    expect,
    test
} from 'vitest';

import { nonNegativeModulo } from '../../src/utils/nonNegativeModulo';

describe('nonNegativeModulo', () => {
    test('simple', () => {
        expect(nonNegativeModulo(3, 10)).toBe(3);
        expect(nonNegativeModulo(23, 10)).toBe(3);
        expect(nonNegativeModulo(-3, 10)).toBe(7);
        expect(nonNegativeModulo(-13, 10)).toBe(7);
        expect(nonNegativeModulo(0, 10)).toBe(0);
        expect(nonNegativeModulo(10, 10)).toBe(0);
    });
});
