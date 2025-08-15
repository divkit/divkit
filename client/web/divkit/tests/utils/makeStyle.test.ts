import {
    describe,
    expect,
    test
} from 'vitest';

import { makeStyle } from '../../src/utils/makeStyle';

describe('makeStyle', () => {
    test('simple', () => {
        expect(makeStyle(undefined)).toBe(undefined);

        expect(makeStyle({})).toBe(undefined);

        expect(makeStyle({
            a: '1'
        })).toBe('a:1');

        expect(makeStyle({
            a: '1',
            b: '2'
        })).toBe('a:1;b:2');
    });
});
