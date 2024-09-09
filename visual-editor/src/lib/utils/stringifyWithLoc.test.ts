import { describe, test, expect, vi } from 'vitest';
import { stringifyWithLoc } from './stringifyWithLoc';

describe('stringifyWithLoc', () => {
    test('simple', () => {
        expect(stringifyWithLoc(null)).toEqual('null');
        expect(stringifyWithLoc(undefined)).toEqual('');
        expect(stringifyWithLoc(false)).toEqual('false');
        expect(stringifyWithLoc(true)).toEqual('true');
        expect(stringifyWithLoc(123)).toEqual('123');
        expect(stringifyWithLoc('hello')).toEqual('"hello"');
        expect(stringifyWithLoc(NaN)).toEqual('null');
        expect(stringifyWithLoc({})).toEqual('{}');
        expect(stringifyWithLoc([])).toEqual('[]');
    });

    test('objects', () => {
        expect(stringifyWithLoc({ a: 1 })).toEqual('{"a":1}');
        expect(stringifyWithLoc({ a: 1 }, null, 4)).toEqual('{\n    "a": 1\n}');
    });

    test('arrays', () => {
        expect(stringifyWithLoc([1, 2])).toEqual('[1,2]');
        expect(stringifyWithLoc([1, 2], null, 4)).toEqual('[\n    1,\n    2\n]');
    });

    test('loc', () => {
        const onLoc = vi.fn();
        expect(stringifyWithLoc({ a: 1 }, null, undefined, onLoc)).toEqual('{"a":1}');
        expect(onLoc.mock.calls).toMatchSnapshot();
    });

    test('loc nested', () => {
        const onLoc = vi.fn();
        expect(stringifyWithLoc({ a: { b: 2 } }, null, undefined, onLoc)).toEqual('{"a":{"b":2}}');
        expect(onLoc.mock.calls).toMatchSnapshot();
    });
});
