import {
    describe,
    expect,
    test
} from 'vitest';

import { genClassName } from '../../src/utils/genClassName';

describe('genClassName', () => {
    test('simple', () => {
        expect(genClassName('test', {
            test: 'hello'
        }, {})).toBe('hello');

        expect(genClassName('test', {
            test: 'hello',
            test_a_1: 'hello_a_1',
            test_b: 'hello_b'
        }, {
            a: '1',
            b: true,
            c: 'yes'
        })).toBe('hello hello_a_1 hello_b');

        expect(genClassName('test', {
            test: 'hello',
            test_a_1: 'hello_a_1',
            test_b: 'hello_b'
        }, {
            a: '2',
            b: false
        })).toBe('hello');
    });
});
