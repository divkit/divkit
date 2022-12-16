import { getUrlSchema, isBuiltinSchema } from '../../src/utils/url';

describe('getUrlSchema', () => {
    test('correct', () => {
        expect(getUrlSchema('http://ya.ru')).toBe('http');
        expect(getUrlSchema('https://ya.ru')).toBe('https');
        expect(getUrlSchema('https://smth')).toBe('https');
        expect(getUrlSchema('div-smth://smth')).toBe('div-smth');
    });

    test('incorrect', () => {
        expect(getUrlSchema('1')).toBe('');
        expect(getUrlSchema('/http://ya.ru')).toBe('');
        expect(getUrlSchema('://ya.ru')).toBe('');
        expect(getUrlSchema('//ya.ru')).toBe('');
    });
});

describe('isBuiltinSchema', () => {
    test('simple', () => {
        const builtinSchemas = new Set(['http', 'https', 'tel', 'mailto', 'intent']);

        expect(isBuiltinSchema('http', builtinSchemas)).toBe(true);
        expect(isBuiltinSchema('mailto', builtinSchemas)).toBe(true);
        expect(isBuiltinSchema('div-smth', builtinSchemas)).toBe(false);
        expect(isBuiltinSchema('custom-schema', builtinSchemas)).toBe(false);
    });
});
