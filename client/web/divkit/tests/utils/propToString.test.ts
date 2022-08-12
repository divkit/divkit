import { propToString } from '../../src/utils/propToString';

describe('expressions', () => {
    test('valToString', () => {
        expect(propToString('a', 'b')).toBe('a');
        expect(propToString(1, 'b')).toBe('1');
        expect(propToString(0, 'b')).toBe('0');
        expect(propToString(undefined, 'b')).toBe('b');
        expect(propToString('', 'b')).toBe('b');
    });
});
