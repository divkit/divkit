/* eslint-disable max-depth */
/* eslint-disable no-else-return */

import type {
    BinaryExpression, BooleanLiteral, CallExpression, CompareOperator,
    ConditionalExpression, EqualityOperator, FactorOperator, IntegerLiteral,
    LogicalExpression,
    MethodExpression,
    Node, NumberLiteral, StringLiteral, SumOperator,
    TemplateLiteral,
    TryExpression,
    UnaryExpression, Variable
} from './ast';
import type { WrappedError } from '../utils/wrapError';
import { convertArgs, findBestMatchedFunc, Func, funcByArgs, funcs, methodByArgs } from './funcs/funcs';
import {
    checkIntegerOverflow,
    evalError,
    integerToNumber,
    roundInteger,
    typeToString,
    valToInternal,
    valToPreview,
    valToString
} from './utils';
import { BOOLEAN, DATETIME, INTEGER, NUMBER, STRING } from './const';
import { register } from './funcs';
import { Variable as VariableInstance, variableToValue } from './variable';
import { toBigInt } from './bigint';
import { wrapError } from '../utils/wrapError';
import type { Store } from '../../typings/store';

export type VariablesMap = Map<string, VariableInstance>;

export type EvalTypes = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'datetime' | 'dict' | 'array';

export type EvalTypesWithoutDatetime = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'dict' | 'array';

export interface EvalValueBase {
    type: string;
    value: unknown;
}

export interface StringValue extends EvalValueBase {
    type: 'string';
    value: string;
}

export interface UrlValue extends EvalValueBase {
    type: 'url';
    value: string;
}

export interface ColorValue extends EvalValueBase {
    type: 'color';
    value: string;
}

export interface NumberValue extends EvalValueBase {
    type: 'number';
    value: number;
}

export interface IntegerValue extends EvalValueBase {
    type: 'integer';
    value: number | bigint;
}

export interface BooleanValue extends EvalValueBase {
    type: 'boolean';
    value: number;
}

export interface DatetimeValue extends EvalValueBase {
    type: 'datetime';
    value: Date;
}

export interface DictValue extends EvalValueBase {
    type: 'dict';
    value: object;
}

export interface ArrayValue extends EvalValueBase {
    type: 'array';
    value: unknown[];
}

export type EvalValue = StringValue | UrlValue | ColorValue | NumberValue | IntegerValue |
    BooleanValue | DatetimeValue | DictValue | ArrayValue;

export interface EvalError {
    type: 'error';
    value: string;
}

export type EvalResult = EvalValue | EvalError;

export interface EvalContext {
    variables: VariablesMap;
    warnings: WrappedError[];
    safeIntegerOverflow: boolean;
    store?: Store;
    weekStartDay: number;
}

register();

function evalStringLiteral(ctx: EvalContext, expr: StringLiteral): EvalValue {
    return {
        type: STRING,
        value: expr.value
    };
}

function evalNumberLiteral(ctx: EvalContext, expr: NumberLiteral): EvalValue {
    return {
        type: NUMBER,
        value: expr.value
    };
}

function evalIntegerLiteral(ctx: EvalContext, expr: IntegerLiteral): EvalValue {
    checkIntegerOverflow(ctx, expr.value);

    return {
        type: INTEGER,
        value: expr.value
    };
}

function evalBooleanLiteral(ctx: EvalContext, expr: BooleanLiteral): EvalValue {
    return {
        type: BOOLEAN,
        value: expr.value ? 1 : 0
    };
}

function evalUnary(ctx: EvalContext, expr: UnaryExpression): EvalValue {
    const val = valToInternal(evalAny(ctx, expr.argument));

    switch (expr.operator) {
        case '!':
            if (val.type === BOOLEAN) {
                return {
                    type: BOOLEAN,
                    value: val.value ? 0 : 1
                };
            } else {
                evalError(`${expr.operator}${valToPreview(val)}`, 'A Boolean is expected after a unary not.');
            }
        case '+':
        case '-':
            const mul = expr.operator === '+' ? 1 : -1;

            if (val.type === INTEGER) {
                const value = (val.value as bigint) * (toBigInt(mul) as bigint);

                checkIntegerOverflow(ctx, value);

                return {
                    type: INTEGER,
                    value
                };
            } else if (val.type === NUMBER) {
                return {
                    type: NUMBER,
                    value: val.value * mul
                };
            } else {
                evalError(
                    `${expr.operator}${valToPreview(val)}`,
                    `A Number is expected after a unary ${expr.operator === '+' ? 'plus' : 'minus'}.`
                );
            }
    }
}

function evalConditional(ctx: EvalContext, expr: ConditionalExpression): EvalValue {
    const test = valToInternal(evalAny(ctx, expr.test));
    if (test.type === BOOLEAN) {
        if (test.value) {
            return evalAny(ctx, expr.consequent);
        } else {
            return evalAny(ctx, expr.alternate);
        }
    } else {
        evalError(
            `${valToPreview(test)} ? ${valToPreview(evalAny(ctx, expr.consequent))} : ${valToPreview(evalAny(ctx, expr.alternate))}`,
            'Ternary must be called with a Boolean value as a condition.'
        );
    }
}

function evalTry(ctx: EvalContext, expr: TryExpression): EvalValue {
    try {
        return evalAny(ctx, expr.test);
    } catch (_err) {
        return evalAny(ctx, expr.alternate);
    }
}

function evalTemplateLiteral(ctx: EvalContext, expr: TemplateLiteral): EvalValue {
    let result = '';

    if (expr.quasis.length === 2 && expr.quasis[0].value === '' && expr.quasis[1].value === '') {
        return evalAny(ctx, expr.expressions[0]);
    }

    for (let i = 0; i < expr.expressions.length; ++i) {
        result += expr.quasis[i].value;
        result += valToString(evalAny(ctx, expr.expressions[i]));
    }
    result += expr.quasis[expr.quasis.length - 1].value;

    return {
        type: STRING,
        value: result
    };
}

function evalLogicalExpression(ctx: EvalContext, expr: LogicalExpression): EvalValue {
    const left = valToInternal(evalAny(ctx, expr.left));
    if (left.type !== BOOLEAN) {
        evalError(
            `${valToPreview(left)} ${expr.operator} ...`,
            `'${expr.operator}' must be called with boolean operands.`
        );
    }

    if (expr.operator === '||' && left.value) {
        return left;
    }
    if (expr.operator === '&&' && !left.value) {
        return {
            type: BOOLEAN,
            value: 0
        };
    }

    const right = valToInternal(evalAny(ctx, expr.right));
    if (right.type !== BOOLEAN) {
        evalError(
            `${valToPreview(left)} ${expr.operator} ${valToPreview(right)}`,
            `Operator '${expr.operator}' cannot be applied to different types: Boolean and ${typeToString(right.type)}.`
        );
    }

    return {
        type: BOOLEAN,
        value: right.value
    };
}

function evalBinaryEquality<T extends EvalValue>(operator: EqualityOperator, left: T, right: T): EvalValue {
    let res: boolean;

    if (left.type === DATETIME && right.type === DATETIME) {
        res = left.value.getTime() === right.value.getTime();
    } else {
        res = left.value === right.value;
    }

    if (operator === '!=') {
        res = !res;
    }

    return {
        type: BOOLEAN,
        value: res ? 1 : 0
    };
}

function evalBinaryCompare<T extends EvalValue>(operator: CompareOperator, left: T, right: T): EvalValue {
    if (left.type !== NUMBER && left.type !== INTEGER && left.type !== DATETIME) {
        evalError(
            `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
            `Operator '${operator}' cannot be applied to ${typeToString(left.type)} type.`
        );
    }

    let res: boolean;
    const leftVal = (left.type === DATETIME ? left.value.getTime() : left.value) as number;
    const rightVal = (right.type === DATETIME ? right.value.getTime() : right.value) as number;

    if (operator === '>') {
        res = leftVal > rightVal;
    } else if (operator === '>=') {
        res = leftVal >= rightVal;
    } else if (operator === '<') {
        res = leftVal < rightVal;
    } else {
        res = leftVal <= rightVal;
    }

    return {
        type: BOOLEAN,
        value: res ? 1 : 0
    };
}

function evalBinarySum<T extends EvalValue>(ctx: EvalContext, operator: SumOperator, left: T, right: T): EvalValue {
    if (left.type !== STRING && left.type !== NUMBER && left.type !== INTEGER) {
        evalError(
            `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
            `Operator '${operator}' cannot be applied to ${typeToString(left.type)} type.`
        );
    }

    if (left.type === STRING) {
        if (operator === '-') {
            evalError(
                `${valToPreview(left)} - ${valToPreview(right)}`,
                `Operator '${operator}' cannot be applied to ${typeToString(left.type)} type.`
            );
        }
        return {
            type: STRING,
            value: left.value + right.value
        };
    }

    let res: number | bigint = operator === '+' ?
        (left.value as bigint) + (right.value as bigint) :
        (left.value as bigint) - (right.value as bigint);

    // integer
    if (left.type === INTEGER) {
        try {
            res = roundInteger(ctx, res);
            checkIntegerOverflow(ctx, res);
        } catch (err: any) {
            evalError(
                `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
                err.message
            );
        }
    }

    return {
        type: left.type,
        value: res as any
    };
}

function evalBinaryFactor<T extends EvalValue>(
    ctx: EvalContext,
    operator: FactorOperator,
    left: T,
    right: T
): EvalValue {
    if (left.type !== INTEGER && left.type !== NUMBER) {
        evalError(
            `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
            `Operator '${operator}' cannot be applied to ${typeToString(left.type)} type.`
        );
    }

    let res: number | bigint;
    if (operator === '*') {
        // bigint | number actually
        res = (left.value as bigint) * (right.value as bigint);
    } else if (operator === '/' || operator === '%') {
        if (Number(right.value) === 0) {
            evalError(
                `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
                'Division by zero is not supported.'
            );
        }
        if (operator === '/') {
            // bigint | number actually
            res = (left.value as bigint) / (right.value as bigint);
        } else {
            // bigint | number actually
            res = (left.value as bigint) % (right.value as bigint);
        }
    } else {
        throw new Error(`Unsupported operation ${operator}`);
    }

    if (left.type === INTEGER) {
        try {
            res = roundInteger(ctx, res);
            checkIntegerOverflow(ctx, res);
        } catch (err: any) {
            evalError(
                `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
                err.message
            );
        }
    }

    return {
        type: left.type,
        value: res as any
    };
}

function evalBinaryExpression(ctx: EvalContext, expr: BinaryExpression): EvalValue {
    const operator = expr.operator;
    let left = evalAny(ctx, expr.left);
    let right = evalAny(ctx, expr.right);

    if (
        left.type === 'number' && right.type === 'integer' ||
        left.type === 'integer' && right.type === 'number'
    ) {
        if (left.type === 'integer') {
            left = integerToNumber(left);
        } else if (right.type === 'integer') {
            right = integerToNumber(right);
        }
    }

    if (left.type !== right.type) {
        evalError(
            `${valToPreview(left)} ${expr.operator} ${valToPreview(right)}`,
            `Operator '${operator}' cannot be applied to different types: ${typeToString(left.type)} and ${typeToString(right.type)}.`
        );
    }

    if (operator === '==' || operator === '!=') {
        return evalBinaryEquality(operator, left, right);
    } else if (operator === '>' || operator === '>=' || operator === '<' || operator === '<=') {
        return evalBinaryCompare(operator, left, right);
    } else if (operator === '+' || operator === '-') {
        return evalBinarySum(ctx, operator, left, right);
    } else if (operator === '/' || operator === '*' || operator === '%') {
        return evalBinaryFactor(ctx, operator, left, right);
    }

    throw new Error(`Unsupported operation ${operator}`);
}

function argsToStr(args: EvalValue[]): string {
    return args.map(valToPreview).join(', ');
}

function evalCallExpression(ctx: EvalContext, expr: CallExpression): EvalValue {
    const funcName = expr.callee.name;

    let func: Func | undefined;

    let args = expr.arguments.map(arg => evalAny(ctx, arg));
    const funcKey = funcName + ':' + args.map(arg => arg.type).join('#');

    if (!funcByArgs.has(funcKey)) {
        const findRes = findBestMatchedFunc('function', funcName, args);
        if ('expected' in findRes || 'type' in findRes && findRes.type === 'missing') {
            const argsType = args.map(arg => typeToString(arg.type)).join(', ');
            const prefix = `${funcName}(${argsToStr(args)})`;
            const funcList = funcs.get(funcName) || [];
            const firstFunc = funcList[0];
            const hasOverloads = funcList.length > 1;

            if (findRes.type === 'few' && args.length === 0 && hasOverloads) {
                evalError(prefix, 'Function requires non empty argument list.');
            } else if (firstFunc && (findRes.type === 'many' || findRes.type === 'few' || findRes.type === 'mismatch')) {
                if (hasOverloads) {
                    evalError(prefix, `Function has no matching overload for given argument types: ${argsType}.`);
                } else {
                    // eslint-disable-next-line no-lonely-if
                    if (findRes.type === 'many' || findRes.type === 'few') {
                        if (firstFunc.args.some(arg => typeof arg === 'object' && arg.isVararg)) {
                            evalError(prefix, `At least ${firstFunc.args.length} argument(s) expected.`);
                        } else {
                            evalError(prefix, `Exactly ${firstFunc.args.length} argument(s) expected.`);
                        }
                    } else {
                        const expectedArgs = firstFunc.args.map(arg => typeToString(typeof arg === 'string' ? arg : arg.type)).join(', ');
                        evalError(prefix, `Invalid argument type: expected ${expectedArgs}, got ${argsType}.`);
                    }
                }
            } else {
                evalError(prefix, `Unknown function name: ${funcName}.`);
            }
        }
        func = findRes.func;

        if (findRes.conversions) {
            args = convertArgs(func, args);
        }
    } else {
        func = funcByArgs.get(funcKey);
    }

    if (!func) {
        throw new Error('Function not found');
    }

    try {
        return func.cb(ctx, ...args);
    } catch (err: any) {
        const prefix = `${funcName}(${argsToStr(args)})`;
        evalError(prefix, err.message);
    }
}

function evalMethodExpression(ctx: EvalContext, expr: MethodExpression): EvalValue {
    const methodName = expr.method.name;

    let func: Func | undefined;

    expr.arguments.unshift(expr.object);

    let args = expr.arguments.map(arg => evalAny(ctx, arg));
    const methodKey = methodName + ':' + args.map(arg => arg.type).join('#');

    if (!methodByArgs.has(methodKey)) {
        const findRes = findBestMatchedFunc('method', methodName, args);
        if ('expected' in findRes || 'type' in findRes && findRes.type === 'missing') {
            const argsType = args.slice(1).map(arg => typeToString(arg.type)).join(', ');
            const prefix = `${methodName}(${argsToStr(args.slice(1))})`;

            if (findRes.type === 'few' && args.length === 1) {
                evalError(prefix, 'Method requires non empty argument list.');
            } else if (findRes.type === 'many') {
                evalError(prefix, `Method has no matching overload for given argument types: ${argsType}.`);
            } else if (findRes.type === 'few' || findRes.type === 'mismatch') {
                evalError(prefix, `Method has no matching overload for given argument types: ${argsType}.`);
            } else {
                evalError(prefix, `Unknown method name: ${methodName}.`);
            }
        }
        func = findRes.func;

        if (findRes.conversions) {
            args = convertArgs(func, args);
        }
    } else {
        func = methodByArgs.get(methodKey);
    }

    if (!func) {
        throw new Error('Method not found');
    }

    try {
        return func.cb(ctx, ...args);
    } catch (err: any) {
        const prefix = `${methodName}(${argsToStr(args.slice(1))})`;
        evalError(prefix, err.message);
    }
}

function evalVariable(ctx: EvalContext, expr: Variable): EvalValue {
    const varName = expr.id.name;
    const variable = ctx.variables.get(varName);

    if (variable) {
        return variableToValue(variable);
    }

    throw new Error(`Variable '${varName}' is missing.`);
}

const EVAL_MAP = {
    StringLiteral: evalStringLiteral,
    NumberLiteral: evalNumberLiteral,
    IntegerLiteral: evalIntegerLiteral,
    BooleanLiteral: evalBooleanLiteral,
    UnaryExpression: evalUnary,
    ConditionalExpression: evalConditional,
    TryExpression: evalTry,
    TemplateLiteral: evalTemplateLiteral,
    LogicalExpression: evalLogicalExpression,
    BinaryExpression: evalBinaryExpression,
    CallExpression: evalCallExpression,
    MethodExpression: evalMethodExpression,
    Variable: evalVariable
};

export function evalAny(ctx: EvalContext, expr: Node): EvalValue {
    if (expr.type in EVAL_MAP) {
        return EVAL_MAP[expr.type](ctx, expr as any);
    }
    throw new Error('Unsupported expression');
}

export function evalExpression(vars: VariablesMap, store: Store | undefined, expr: Node, opts?: {
    weekStartDay?: number;
}): {
    result: EvalResult;
    warnings: WrappedError[];
} {
    try {
        const ctx: EvalContext = {
            variables: vars,
            warnings: [],
            safeIntegerOverflow: false,
            store,
            weekStartDay: opts?.weekStartDay || 0
        };

        const result = evalAny(ctx, expr);

        if (ctx.safeIntegerOverflow) {
            ctx.warnings.push(wrapError(new Error('Safe integer overflow, values may lose accuracy.'), {
                level: 'warn'
            }));
        }

        return {
            result,
            warnings: ctx.warnings
        };
    } catch (err: any) {
        return {
            result: {
                type: 'error',
                value: err.message
            },
            warnings: []
        };
    }
}
