import { describe, test, expect } from 'vitest';
import { htmlFilter } from './htmlFilter';

describe('htmlFilter', () => {
    test('simple', () => {
        expect(htmlFilter('')).toEqual('');
        expect(htmlFilter('Hello')).toEqual('Hello');
        expect(htmlFilter('<div>')).toEqual('&lt;div&gt;');
        expect(htmlFilter('&amp;')).toEqual('&amp;amp;');
        expect(htmlFilter('"')).toEqual('&quot;');
    });
});
