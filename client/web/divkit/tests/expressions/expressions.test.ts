/* eslint-disable max-depth */
import {
    describe,
    expect,
    test
} from 'vitest';

import { evalExpression, type EvalResult } from '../../src/expressions/eval';
import { transformColorValue, valToString } from '../../src/expressions/utils';
import { parse } from '../../src/expressions/expressions';
import { createVariable } from '../../src/expressions/variable';
import { customFunctionWrap, type CustomFunctions } from '../../src/expressions/funcs/customFuncs';
import type { DivFunction } from '../../typings/common';

const path = require('path');
const fs = require('fs');

const dir = path.resolve(__filename, '../../../../../../test_data/expression_test_data');

const tests = fs.readdirSync(dir);

function convertVals(val: EvalResult) {
    if (val.type === 'boolean') {
        return {
            type: 'boolean',
            value: val.value ? 1 : 0
        };
    } else if (val.type === 'integer') {
        return {
            type: 'integer',
            // values in json is out of range already
            value: Number(val.value)
        };
    } else if (val.type === 'datetime' && val.value instanceof Date) {
        return {
            type: 'datetime',
            value: valToString(val, false)
        };
    } else if (val.type === 'color') {
        return {
            type: 'color',
            value: transformColorValue(val.value)
        };
    }

    return val;
}

function runCase(item: any) {
    const vars = new Map();
    const customFunctions: CustomFunctions = new Map();
    if (item.variables) {
        for (const variable of item.variables) {
            let value;

            if (typeof variable.value === 'string') {
                let ast;
                try {
                    ast = parse(variable.value, {
                        startRule: 'JsonStringContents'
                    });
                } catch (err) {
                    if (item.expected.type === 'error') {
                        if (item.expected.value) {
                            expect(err instanceof Error ? err.message : String(err)).toEqual(item.expected.value);
                        }
                        return;
                    }

                    expect('error').toEqual(item.expected.type);
                    return;
                }
                const res = evalExpression(vars, undefined, undefined, ast, {
                    weekStartDay: item.platform_specific?.web?.weekStartDay || 0
                }).result;
                if (res.type === 'error') {
                    if (item.expected.type === 'error') {
                        if (item.expected.value) {
                            expect(res.value).toEqual(item.expected.value);
                        }
                        return;
                    }

                    expect(res.type).toEqual(item.expected.type);
                } else {
                    value = res.value;
                }
            } else {
                value = variable.value;
            }

            vars.set(variable.name, createVariable(variable.name, variable.type, value));
        }
    }
    if (item.functions) {
        for (const func of item.functions) {
            const fn = func as DivFunction;
            const list = customFunctions.get(fn.name) || [];
            list.push(customFunctionWrap(fn));
            customFunctions.set(fn.name, list);
        }
    }
    let ast;
    try {
        ast = parse(item.expression, {
            startRule: 'JsonStringContents'
        });
    } catch (err: any) {
        if (item.expected.value) {
            expect({
                type: 'error',
                value: err.message
            }).toEqual(item.expected);
        } else {
            expect('error').toEqual(item.expected.type);
        }
        return;
    }
    const res = evalExpression(vars, customFunctions, undefined, ast, {
        weekStartDay: item.platform_specific?.web?.weekStartDay || 0
    });
    if (item.expected.value !== '' || res.result.type !== 'error') {
        if (item.expected.type === 'integer') {
            expect(res.result.value).toBeTypeOf('bigint');
        }

        if (res.result.type === 'array' && item.expected.type === 'unordered_array') {
            expect(new Set(res.result.value.map(value => ({ value }))))
                .toEqual(new Set(item.expected.value.map((value: unknown) => ({ value }))));
        } else if (res.result.type === 'number' && item.expected.type === 'number') {
            expect(res.result.value).toBeCloseTo(item.expected.value);
        } else {
            expect(convertVals(res.result)).toEqual(convertVals(item.expected));
        }
    } else {
        expect(res.result.type).toEqual(item.expected.type);
    }
    if (res.warnings.length || item.expected_warnings) {
        expect(res.warnings.map(it => it.message)).toEqual(item.expected_warnings);
    }
}

interface Test {
    name?: string;
    expression: string;
    expected: {
        type: 'error' | 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'datetime' | 'array' | 'dict';
        value: unknown;
    };
}

function getTestName(test: Test): string {
    if (test.name) {
        return test.name;
    }

    let expr;
    if (test.expression.startsWith('@{') && test.expression.endsWith('}') && !test.expression.slice(2).includes('@{')) {
        expr = test.expression.slice(2, test.expression.length - 1);
    } else {
        expr = test.expression;
    }

    let expected;
    if (test.expected.type === 'error') {
        expected = 'error';
    } else if (test.expected.type === 'array') {
        expected = '<array>';
    } else if (test.expected.type === 'dict') {
        expected = '<dict>';
    } else {
        expected = String(test.expected.value);
    }

    return `${expr} => ${expected}`;
}

describe('expressions', () => {
    for (const file of tests) {
        const name = file.replace('.json', '');
        const fullpath = path.resolve(dir, file);
        try {
            const contents = require(fullpath);

            if (contents.cases) {
                const cases = contents.cases.filter((it: any) => it.platforms.includes('web'));
                const counter: Record<string, number> = {};

                if (cases.length) {
                    describe(name, () => {
                        for (const item of contents.cases) {
                            if (item.platforms.includes('web')) {
                                let name = getTestName(item);

                                if (!counter[name]) {
                                    counter[name] = 0;
                                }

                                name += ` : ${counter[name]++}`;

                                test(name, () => {
                                    runCase(item);
                                });
                            } else {
                                // eslint-disable-next-line no-console
                                console.log('skip', file, name, item.name);
                            }
                        }
                    });
                }
            }
        } catch (err) {
            console.error('Failed to process', fullpath, err);
        }
    }
});
