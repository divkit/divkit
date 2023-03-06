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
    const result = evalExpression(vars, ast);
    if (item.expected.value !== '' || result.type !== 'error') {
        expect(convertVals(result)).toEqual(convertVals(item.expected));
    } else {
        expect(result.type).toEqual(item.expected.type);
    }
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
                        let name = item.name;

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
