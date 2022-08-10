import { uniq } from '../../src/utils/uniq';

describe('uniq', () => {
    test('simple', () => {
        expect(uniq([])).toStrictEqual([]);
        expect(uniq(['a'])).toStrictEqual(['a']);
        expect(uniq(['a', 'b'])).toStrictEqual(['a', 'b']);
        expect(uniq(['a', 'a'])).toStrictEqual(['a']);
        expect(uniq(['a', 'b', 'a'])).toStrictEqual(['a', 'b']);
    });
});
