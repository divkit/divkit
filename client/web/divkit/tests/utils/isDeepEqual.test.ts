import { isDeepEqual } from '../../src/utils/isDeepEqual';

describe('isDeepEqual', () => {
    test('simple', () => {
        expect(isDeepEqual(0, 0)).toBe(true);
        expect(isDeepEqual(0, 1)).toBe(false);
        expect(isDeepEqual('0', '0')).toBe(true);
        expect(isDeepEqual(0, '0')).toBe(false);
        expect(isDeepEqual(false, false)).toBe(true);
        expect(isDeepEqual(0, false)).toBe(false);
        expect(isDeepEqual(false, '')).toBe(false);
        expect(isDeepEqual(0, null)).toBe(false);
        expect(isDeepEqual(0, undefined)).toBe(false);
        expect(isDeepEqual(null, null)).toBe(true);
        expect(isDeepEqual(undefined, undefined)).toBe(true);
    });

    test('objects', () => {
        expect(isDeepEqual({ a: 1 }, { a: 1 })).toBe(true);
        expect(isDeepEqual({ a: 1 }, { a: 2 })).toBe(false);
        expect(isDeepEqual({
            a: {
                b: 1
            }
        }, {
            a: {
                b: 1
            }
        })).toBe(true);
        expect(isDeepEqual({
            a: {
                b: 1
            }
        }, {
            a: {
                b: 2
            }
        })).toBe(false);
        expect(isDeepEqual({
            a: {
                b: 1
            }
        }, {
            a: {
                c: 1
            }
        })).toBe(false);
        expect(isDeepEqual({
            a: {
                b: 1
            }
        }, {
            a: {
                b: 1,
                c: 2
            }
        })).toBe(false);
        expect(isDeepEqual({
            a: {
                b: 1,
                c: 2
            }
        }, {
            a: {
                b: 1
            }
        })).toBe(false);
    });

    test('arrays', () => {
        expect(isDeepEqual([], [])).toBe(true);
        expect(isDeepEqual([], [1])).toBe(false);
        expect(isDeepEqual([1], [1])).toBe(true);
        expect(isDeepEqual([{ a: 1 }], [{ a: 1 }])).toBe(true);
        expect(isDeepEqual([{ a: 1 }], [{ a: 2 }])).toBe(false);
        expect(isDeepEqual([{ a: 1 }], [{ a: 2 }])).toBe(false);
    });
});
