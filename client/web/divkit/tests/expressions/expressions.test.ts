import { evalExpression, EvalResult } from '../../src/expressions/eval';
import { valToString } from '../../src/expressions/utils';
import { parse } from '../../src/expressions/expressions';
import { createVariable } from '../../src/expressions/variable';

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
            value: valToString(val)
        };
    }

    return val;
}

function runCase(item: any) {
    const vars = new Map();
    if (item.variables) {
        for (const variable of item.variables) {
            vars.set(variable.name, createVariable(variable.name, variable.type, variable.value));
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
    const res = evalExpression(vars, undefined, ast, {
        weekStartDay: item.platform_specific?.web?.weekStartDay || 0
    });
    if (item.expected.value !== '' || res.result.type !== 'error') {
        if (res.result.type === 'number' && item.expected.type === 'number') {
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
        const contents = require(path.resolve(dir, file));

        if (contents.cases) {
            const counter: Record<string, number> = {};

            describe(name, () => {
                for (const item of contents.cases) {
                    if (item.platforms.includes('web')) {
                        let name = getTestName(item);

                        if (!counter[name]) {
                            counter[name] = 0;
                        }

                        name += ` : ${counter[name]++}`;

                        it(name, () => {
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
});
