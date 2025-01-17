import {
    describe,
    expect,
    test
} from 'vitest';

import { htmlFilter } from '../../src/utils/htmlFilter';

describe('htmlFilter', () => {
    test('simple', () => {
        expect(htmlFilter('abc')).toBe('abc');

        expect(htmlFilter('<script>')).toBe('&lt;script&gt;');

        expect(htmlFilter('a " b & c')).toBe('a &quot; b &amp; c');
    });
});
