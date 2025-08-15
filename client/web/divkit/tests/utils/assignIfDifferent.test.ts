import {
    describe,
    expect,
    test
} from 'vitest';

import { assignIfDifferent } from '../../src/utils/assignIfDifferent';

describe('assignIfDifferent', () => {
    test('simple', () => {
        expect(assignIfDifferent(1, 2)).toBe(1);
        expect(assignIfDifferent(1, 1)).toBe(1);
    });

    test('obj', () => {
        const a = {
            a: 1
        };
        const a2 = {
            a: 1
        };
        const b = {
            b: 1
        };
        expect(assignIfDifferent(a, a)).toBe(a);
        expect(assignIfDifferent(a, a2)).toBe(a2);
        expect(assignIfDifferent(a2, a)).toBe(a);
        expect(assignIfDifferent(a, b)).toBe(a);
        expect(assignIfDifferent(b, a)).toBe(b);
    });
});
