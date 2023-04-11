import { simpleUnescapeString } from '../../src/expressions/simpleUnescapeString';

describe('simpleUnescapeString', () => {
    test('valid', () => {
        expect(simpleUnescapeString('abc')).toEqual('abc');
        expect(simpleUnescapeString('123')).toEqual('123');
        expect(simpleUnescapeString('{}')).toEqual('{}');
        expect(simpleUnescapeString('a@')).toEqual('a@');
        expect(simpleUnescapeString('a@b')).toEqual('a@b');
    });

    test('simple escape', () => {
        expect(simpleUnescapeString('\\\\')).toEqual('\\');
        expect(simpleUnescapeString('\\@{')).toEqual('@{');
        expect(simpleUnescapeString('@\\\\')).toEqual('@\\');
    });

    test('invalid escape', () => {
        expect(() => {
            simpleUnescapeString('\\a');
        }).toThrow('Incorrect escape');
        expect(() => {
            simpleUnescapeString('\\n');
        }).toThrow('Incorrect escape');
        expect(() => {
            simpleUnescapeString('a\\');
        }).toThrow('Incorrect escape');
    });

    test('expressions', () => {
        expect(() => {
            simpleUnescapeString('@{1}');
        }).toThrow('Expressions is not supported');
        expect(() => {
            simpleUnescapeString('a @{1} b');
        }).toThrow('Expressions is not supported');
    });
});
