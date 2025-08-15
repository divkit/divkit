import {
    describe,
    expect,
    test
} from 'vitest';

import { lerp } from '../../src/utils/lerp';

describe('lerp', () => {
    test('simple', () => {
        expect(lerp(0, 10, 0)).toBe(0);
        expect(lerp(0, 10, 1)).toBe(10);
        expect(lerp(0, 10, 0.5)).toBe(5);
    });
});
