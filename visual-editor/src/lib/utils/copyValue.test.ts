import { describe, test, expect } from 'vitest';
import { copyValue } from './copyValue';

describe('copyValue', () => {
    test('simple', () => {
        expect(copyValue(undefined)).toEqual(undefined);
        expect(copyValue(123)).toEqual(123);
        expect(copyValue({ a: 1 })).toEqual({ a: 1 });
    });
});
