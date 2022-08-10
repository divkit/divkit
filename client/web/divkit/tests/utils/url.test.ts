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
        expect(isBuiltinSchema('http')).toBe(true);
        expect(isBuiltinSchema('mailto')).toBe(true);
        expect(isBuiltinSchema('div-smth')).toBe(false);
        expect(isBuiltinSchema('custom-schema')).toBe(false);
    });
});
