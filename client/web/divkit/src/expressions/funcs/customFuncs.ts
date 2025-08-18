import type { DivFunction } from '../../../typings/common';
import type { Node } from '../ast';
import { evalExpression, type EvalTypes, type VariablesMap } from '../eval';
import { parse } from '../expressions';
import type { MaybeMissing } from '../json';
import { cacheGet, cacheSet } from '../parserCache';
import { createConstVariable, Variable } from '../variable';
import { funcToKey, type Func } from './funcs';

export type CustomFunctions = Map<string, Func[]>;

const supportedTypes = new Set<EvalTypes>([
    'string',
    'integer',
    'number',
    'boolean',
    'datetime',
    'color',
    'url',
    'dict',
    'array'
]);

export function checkCustomFunction(func: MaybeMissing<DivFunction>): void {
    if (!(typeof func.name === 'string' && func.name)) {
        throw new Error('Incorrect function name');
    }

    if (!(typeof func.body === 'string' && func.body)) {
        throw new Error('Incorrect function body');
    }

    if (!(func.return_type && supportedTypes.has(func.return_type))) {
        throw new Error('Incorrect function return_type');
    }

    if (!Array.isArray(func.arguments)) {
        throw new Error('Incorrect function arguments');
    }

    const argumentsNames = new Set<string>();
    func.arguments.forEach(arg => {
        if (!(typeof arg.name === 'string' && arg.name)) {
            throw new Error('Incorrect argument name');
        }

        if (!(arg.type && supportedTypes.has(arg.type))) {
            throw new Error('Incorrect argument type');
        }

        if (argumentsNames.has(arg.name)) {
            throw new Error('Duplicate argument name');
        }
        argumentsNames.add(arg.name);
    });
}

export function customFunctionWrap(fn: DivFunction): Func {
    let ast: Node | undefined;

    return {
        args: fn.arguments.map(it => {
            return {
                type: it.type
            };
        }),
        cb(ctx, ...args) {
            if (!ast) {
                ast = cacheGet(fn.body) || parse(fn.body, {
                    startRule: 'JsonStringContents'
                });
                cacheSet(fn.body, ast);
            }

            const vars: VariablesMap = new Map();
            args.forEach((arg, index) => {
                const instance = createConstVariable(fn.arguments[index].name, arg.type, arg.value);
                // DatetimeVariable doesnt exist right know, but works fine
                vars.set(instance.getName(), instance as Variable);
            });

            const res = evalExpression(vars, ctx.customFunctions, ctx.store, ast, {
                weekStartDay: ctx.weekStartDay
            });
            res.warnings.forEach(warn => {
                ctx.warnings.push(warn);
            });
            const result = res.result;

            if (result.type === 'error') {
                throw new Error(result.value);
            }

            if (result.type !== fn.return_type) {
                throw new Error('Incorrect function return_type');
            }

            return result;
        },
    };
}

export function mergeCustomFunctions(
    map0: CustomFunctions | undefined,
    map1: CustomFunctions | undefined
): CustomFunctions | undefined {
    if (!map0) {
        return map1 || undefined;
    }
    if (!map1) {
        return map0 || undefined;
    }

    const merged = new Map();
    const defined = new Set<string>();
    for (const [name, funcs] of map1) {
        for (const func of funcs) {
            const key = funcToKey(name, func);
            defined.add(key);
        }
        merged.set(name, funcs);
    }
    for (const [name, funcs] of map0) {
        for (const func of funcs) {
            const key = funcToKey(name, func);
            if (!defined.has(key)) {
                defined.add(key);
                const list = merged.get(name) || [];
                list.push(func);
                merged.set(name, list);
            }
        }
    }

    return merged;
}
