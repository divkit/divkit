import { describe, test, expect } from 'vitest';
import { makeStyle } from './makeStyle';

describe('makeStyle', () => {
    test('simple', () => {
        expect(makeStyle(undefined)).toEqual(undefined);
        expect(makeStyle({})).toEqual(undefined);
        expect(makeStyle({ color: 'red' })).toEqual('color:red');
        expect(makeStyle({ color: 'red', width: '100%' })).toEqual('color:red;width:100%');
    });
});
