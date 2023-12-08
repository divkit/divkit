import { propToString } from '../../src/utils/propToString';

describe('expressions', () => {
    test('valToString', () => {
        expect(propToString('a')).toBe('a');
        expect(propToString(1)).toBe('1');
        expect(propToString(0)).toBe('0');
        expect(propToString(undefined)).toBe('');
        expect(propToString('')).toBe('');
    });
});
