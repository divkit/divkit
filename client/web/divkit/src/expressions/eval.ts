/* eslint-disable no-else-return */

import {
    BinaryExpression, BooleanLiteral, CallExpression, CompareOperator,
    ConditionalExpression, EqualityOperator, FactorOperator, IntegerLiteral,
    LogicalExpression,
    Node, NumberLiteral, StringLiteral, SumOperator,
    TemplateLiteral,
    UnaryExpression, Variable
} from './ast';
import { findBestMatchedFunc, Func, funcByArgs, funcs } from './funcs/funcs';
import { evalError, roundInteger, typeToString, valToInternal, valToPreview, valToString } from './utils';
import { BOOLEAN, DATETIME, INTEGER, NUMBER, STRING } from './const';
import { register } from './funcs';
import { Variable as VariableInstance, variableToValue } from './variable';

export type VariablesMap = Map<string, VariableInstance>;

export type EvalTypes = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'datetime';

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
    value: number;
}

export interface BooleanValue extends EvalValueBase {
    type: 'boolean';
    value: number;
}

export interface DatetimeValue extends EvalValueBase {
    type: 'datetime';
    value: Date;
}

export type EvalValue = StringValue | UrlValue | ColorValue | NumberValue | IntegerValue |
    BooleanValue | DatetimeValue;

export interface EvalError {
    type: 'error';
    value: string;
}

export type EvalResult = EvalValue | EvalError;

register();

function evalStringLiteral(vars: VariablesMap, expr: StringLiteral): EvalValue {
    return {
        type: STRING,
        value: expr.value
    };
}

function evalNumberLiteral(vars: VariablesMap, expr: NumberLiteral): EvalValue {
    return {
        type: NUMBER,
        value: expr.value
    };
}

function evalIntegerLiteral(vars: VariablesMap, expr: IntegerLiteral): EvalValue {
    return {
        type: INTEGER,
        value: expr.value
    };
}

function evalBooleanLiteral(vars: VariablesMap, expr: BooleanLiteral): EvalValue {
    return {
        type: BOOLEAN,
        value: expr.value ? 1 : 0
    };
}

function evalUnary(vars: VariablesMap, expr: UnaryExpression): EvalValue {
    const val = valToInternal(evalAny(vars, expr.argument));

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
                return {
                    type: INTEGER,
                    value: val.value * mul
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

function evalConditional(vars: VariablesMap, expr: ConditionalExpression): EvalValue {
    const test = valToInternal(evalAny(vars, expr.test));
    if (test.type === BOOLEAN) {
        if (test.value) {
            return evalAny(vars, expr.consequent);
        } else {
            return evalAny(vars, expr.alternate);
        }
    } else {
        evalError(
            `${valToPreview(test)} ? ${valToPreview(evalAny(vars, expr.consequent))} : ${valToPreview(evalAny(vars, expr.alternate))}`,
            'Ternary must be called with a Boolean value as a condition.'
        );
    }
}

function evalTemplateLiteral(vars: VariablesMap, expr: TemplateLiteral): EvalValue {
    let result = '';

    if (expr.quasis.length === 2 && expr.quasis[0].value === '' && expr.quasis[1].value === '') {
        return evalAny(vars, expr.expressions[0]);
    }

    for (let i = 0; i < expr.expressions.length; ++i) {
        result += expr.quasis[i].value;
        result += valToString(evalAny(vars, expr.expressions[i]));
    }
    result += expr.quasis[expr.quasis.length - 1].value;

    return {
        type: STRING,
        value: result
    };
}

function evalLogicalExpression(vars: VariablesMap, expr: LogicalExpression): EvalValue {
    const left = valToInternal(evalAny(vars, expr.left));
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

    const right = valToInternal(evalAny(vars, expr.right));
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
    const leftVal = left.type === DATETIME ? left.value.getTime() : left.value;
    const rightVal = right.type === DATETIME ? right.value.getTime() : right.value;

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

function evalBinarySum<T extends EvalValue>(operator: SumOperator, left: T, right: T): EvalValue {
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

    let res = operator === '+' ? left.value + (right.value as number) : left.value - (right.value as number);

    // integer
    if (left.type === INTEGER) {
        res = roundInteger(res);
    }

    return {
        type: left.type,
        value: res
    };
}

function evalBinaryFactor<T extends EvalValue>(operator: FactorOperator, left: T, right: T): EvalValue {
    if (left.type !== INTEGER && left.type !== NUMBER) {
        evalError(
            `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
            `Operator '${operator}' cannot be applied to ${typeToString(left.type)} type.`
        );
    }

    let res: number;
    if (operator === '*') {
        res = left.value * (right.value as number);
    } else if (operator === '/' || operator === '%') {
        if (right.value === 0) {
            evalError(
                `${valToPreview(left)} ${operator} ${valToPreview(right)}`,
                'Division by zero is not supported.'
            );
        }
        if (operator === '/') {
            res = left.value / (right.value as number);
        } else {
            res = left.value % (right.value as number);
        }
    } else {
        throw new Error(`Unsupported operation ${operator}`);
    }

    if (left.type === INTEGER) {
        res = roundInteger(res);
    }

    return {
        type: left.type,
        value: res
    };
}

function evalBinaryExpression(vars: VariablesMap, expr: BinaryExpression): EvalValue {
    const operator = expr.operator;
    const left = evalAny(vars, expr.left);
    const right = evalAny(vars, expr.right);

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
        return evalBinarySum(operator, left, right);
    } else if (operator === '/' || operator === '*' || operator === '%') {
        return evalBinaryFactor(operator, left, right);
    }

    throw new Error(`Unsupported operation ${operator}`);
}

function argsToStr(args: EvalValue[]): string {
    return args.map(valToPreview).join(', ');
}

function evalCallExpression(vars: VariablesMap, expr: CallExpression): EvalValue {
    const funcName = expr.callee.name;
    if (!funcs.has(funcName)) {
        throw new Error(`Unknown function name: ${funcName}.`);
    }

    let func: Func | undefined;

    const args = expr.arguments.map(arg => evalAny(vars, arg)).map(valToInternal);
    const funcKey = funcName + ':' + args.map(arg => arg.type).join('#');

    if (!funcByArgs.has(funcKey)) {
        const findRes = findBestMatchedFunc(funcName, args);
        if ('expected' in findRes) {
            const argsType = args.map(arg => typeToString(arg.type)).join(', ');
            const prefix = `${funcName}(${argsToStr(args)})`;

            if (findRes.type === 'few' && args.length === 0) {
                evalError(prefix, `Non empty argument list is required for function '${funcName}'.`);
            } else if (findRes.type === 'many') {
                evalError(prefix, `Function '${funcName}' has no matching override for given argument types: ${argsType}.`);
            } else {
                evalError(prefix, `Function '${funcName}' has no matching override for given argument types: ${argsType}.`);
            }
        }
        func = findRes;
    } else {
        func = funcByArgs.get(funcKey);
    }

    if (!func) {
        throw new Error('Function not found');
    }

    try {
        return func.cb(...args);
    } catch (err: any) {
        const prefix = `${funcName}(${argsToStr(args)})`;
        evalError(prefix, err.message);
    }
}

function evalVariable(vars: VariablesMap, expr: Variable): EvalValue {
    const varName = expr.id.name;
    const variable = vars.get(varName);

    if (variable) {
        return variableToValue(variable);
    }

    throw new Error(`Variable '${varName}' has value null`);
}

const EVAL_MAP = {
    StringLiteral: evalStringLiteral,
    NumberLiteral: evalNumberLiteral,
    IntegerLiteral: evalIntegerLiteral,
    BooleanLiteral: evalBooleanLiteral,
    UnaryExpression: evalUnary,
    ConditionalExpression: evalConditional,
    TemplateLiteral: evalTemplateLiteral,
    LogicalExpression: evalLogicalExpression,
    BinaryExpression: evalBinaryExpression,
    CallExpression: evalCallExpression,
    Variable: evalVariable
};

export function evalAny(vars: VariablesMap, expr: Node): EvalValue {
    if (expr.type in EVAL_MAP) {
        return EVAL_MAP[expr.type](vars, expr as any);
    }
    throw new Error('Unsupported expression');
}

export function evalExpression(vars: VariablesMap, expr: Node): EvalResult {
    try {
        return evalAny(vars, expr);
    } catch (err: any) {
        return {
            type: 'error',
            value: err.message
        };
    }
}
