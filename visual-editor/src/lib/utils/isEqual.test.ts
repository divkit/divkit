import { describe, test, expect } from 'vitest';
import { isEqual } from './isEqual';

describe('isEqual', () => {
    test('simple', () => {
        expect(isEqual(undefined, undefined)).toEqual(true);
        expect(isEqual({ a: 1 }, { a: 1 })).toEqual(true);
        expect(isEqual({ a: 1 }, { a: 2 })).toEqual(false);
        expect(isEqual({ a: 1 }, { })).toEqual(false);
        expect(isEqual({ a: 1 }, { a: 1, b: 2 })).toEqual(false);
        expect(isEqual({ }, { })).toEqual(true);
        expect(isEqual({ }, { a: 2 })).toEqual(false);
    });
});
