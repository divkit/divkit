import {
    describe,
    expect,
    test
} from 'vitest';

import type { WrappedError } from '../../typings/common';
import { prepareVars } from '../../src/expressions/json';
import { IntegerVariable } from '../../src/expressions/variable';

describe('correctPositiveNumber', () => {
    const logError = (err: WrappedError): void => {
        throw err;
    };

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    const noError = (_err: WrappedError): void => {
    };

    test('no vars', () => {
        const prepared = prepareVars({
            prop: 123
        }, logError, undefined, 0);
        expect(prepared.vars).toStrictEqual([]);
        expect(prepared.applyVars(new Map())).toStrictEqual({
            prop: 123
        });
    });

    test('simple', () => {
        const prepared = prepareVars({
            prop: '@{var}'
        }, logError, undefined, 0);
        expect(prepared.vars).toStrictEqual(['var']);
        const map = new Map();
        const variable = new IntegerVariable('var', 123);
        map.set('var', variable);
        const res = prepared.applyVars(map);
        expect(res).toStrictEqual({
            prop: 123
        });
    });

    test('simple no val', () => {
        const prepared = prepareVars({
            prop: '@{var}'
        }, noError, undefined, 0);
        expect(prepared.vars).toStrictEqual(['var']);
        const map = new Map();
        expect(prepared.applyVars(map)).toStrictEqual({
            prop: undefined
        });
    });

    test('array prop', () => {
        const prepared = prepareVars({
            prop: [
                '@{var}',
                'smth',
                456
            ]
        }, logError, undefined, 0);
        expect(prepared.vars).toStrictEqual(['var']);
        const map = new Map();
        const variable = new IntegerVariable('var', 123);
        map.set('var', variable);
        expect(prepared.applyVars(map)).toStrictEqual({
            prop: [
                123,
                'smth',
                456
            ]
        });
    });

    test('interpolation', () => {
        const prepared = prepareVars({
            prop: 'Hello, @{var}'
        }, logError, undefined, 0);
        expect(prepared.vars).toStrictEqual(['var']);
        const map = new Map();
        const variable = new IntegerVariable('var', 123);
        map.set('var', variable);
        expect(prepared.applyVars(map)).toStrictEqual({
            prop: 'Hello, 123'
        });
    });

    test('multiple vars', () => {
        const prepared = prepareVars({
            prop: 'Hello, @{var}. Good bye, @{var2}'
        }, logError, undefined, 0);
        expect(prepared.vars).toStrictEqual(['var', 'var2']);
        const map = new Map();
        const variable = new IntegerVariable('var', 123);
        const variable2 = new IntegerVariable('var2', 456);
        map.set('var', variable);
        map.set('var2', variable2);
        expect(prepared.applyVars(map)).toStrictEqual({
            prop: 'Hello, 123. Good bye, 456'
        });
    });
});
