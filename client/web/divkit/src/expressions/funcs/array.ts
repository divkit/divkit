import { parseColor } from '../../utils/correctColor';
import { toBigInt } from '../bigint';
import { ARRAY, BOOLEAN, COLOR, DICT, FUNCTION, INTEGER, NUMBER, STRING, URL } from '../const';
import { logFunctionMatchError, type ArrayValue, type BooleanValue, type ColorValue, type EvalContext, type EvalTypes, type EvalValue, type FuncValue, type IntegerValue, type NumberValue, type StringValue, type UrlValue } from '../eval';
import { checkIntegerOverflow, checkUrl, convertJsValueToDivKit, safeCheckUrl, transformColorValue, typeToString } from '../utils';
import { findBestMatchedFuncList, registerFunc, registerMethod, type Func, type FuncMatch } from './funcs';

function arrayGetter(jsType: string, runtimeType: string) {
    return (ctx: EvalContext, array: ArrayValue, index: IntegerValue): EvalValue => {
        if (index.value < 0 || index.value >= array.value.length) {
            throw new Error(`Requested index (${index.value}) out of bounds array size (${array.value.length}).`);
        }
        let val = array.value[Number(index.value)];

        let type: string = typeof val;
        if (
            jsType === 'array' && !Array.isArray(val) ||
            jsType !== 'array' && type !== jsType ||
            type === 'object' && val === null
        ) {
            if (type === 'object') {
                if (Array.isArray(val)) {
                    type = 'Array';
                } else if (val === null) {
                    type = 'Null';
                } else {
                    type = 'Dict';
                }
            }
            throw new Error(`Incorrect value type: expected ${typeToString(runtimeType)}, got ${typeToString(type)}.`);
        }
        if (jsType === 'number' && runtimeType === 'integer') {
            checkIntegerOverflow(ctx, val as number);
            try {
                val = toBigInt(val as number);
            } catch (_err) {
                throw new Error('Cannot convert value to integer.');
            }
        }
        if (jsType === 'string' && runtimeType === 'color') {
            val = transformColorValue(val as string);
        }
        if (jsType === 'string' && runtimeType === 'url') {
            checkUrl(val);
        }

        return {
            type: runtimeType,
            value: val
        } as EvalValue;
    };
}

function optWrapper<ValueType extends EvalValue>(
    func: (ctx: EvalContext, array: ArrayValue, index: IntegerValue) => EvalValue,
    fallbackType: EvalTypes
) {
    return (ctx: EvalContext, array: ArrayValue, index: IntegerValue, fallback: ValueType) => {
        try {
            return func(ctx, array, index);
        } catch (_err) {
            // ignore error

            let value = fallback.value;
            if (fallbackType === 'color') {
                value = transformColorValue(value as string);
            } else if (fallbackType === 'url') {
                checkUrl(value);
            }
            return {
                type: fallbackType,
                value
            } as unknown as EvalValue;
        }
    };
}

const getArrayString = arrayGetter('string', 'string');
const getArrayNumber = arrayGetter('number', 'number');
const getArrayInteger = arrayGetter('number', 'integer');
const getArrayBoolean = arrayGetter('boolean', 'boolean');
const getArrayColor = arrayGetter('string', 'color');
const getArrayUrl = arrayGetter('string', 'url');
const getArrayArray = arrayGetter('array', 'array');
const getArrayDict = arrayGetter('object', 'dict');

const getArrayOptString = optWrapper<StringValue>(getArrayString, 'string');
const getArrayOptNumber = optWrapper<NumberValue>(getArrayNumber, 'number');
const getArrayOptInteger = optWrapper<IntegerValue>(getArrayInteger, 'integer');
const getArrayOptBoolean = optWrapper<BooleanValue>(getArrayBoolean, 'boolean');
const getArrayOptColor = optWrapper<ColorValue>(getArrayColor, 'color');
const getArrayOptUrl = optWrapper<UrlValue>(getArrayUrl, 'url');

function getArrayOptArray(ctx: EvalContext, array: ArrayValue, index: IntegerValue): EvalValue {
    try {
        return getArrayArray(ctx, array, index);
    } catch (_err) {
        // ignore error
        return {
            type: ARRAY,
            value: []
        } as unknown as EvalValue;
    }
}

function getArrayOptDict(ctx: EvalContext, array: ArrayValue, index: IntegerValue): EvalValue {
    try {
        return getArrayDict(ctx, array, index);
    } catch (_err) {
        // ignore error
        return {
            type: DICT,
            value: {}
        } as unknown as EvalValue;
    }
}

function len(_ctx: EvalContext, array: ArrayValue): EvalValue {
    return {
        type: INTEGER,
        value: toBigInt(array.value.length)
    };
}

function isEmpty(_ctx: EvalContext, array: ArrayValue): EvalValue {
    return {
        type: BOOLEAN,
        value: array.value.length === 0 ? 1 : 0
    };
}

function filter(ctx: EvalContext, array: ArrayValue, fn: FuncValue): EvalValue {
    if (!array.value.length) {
        return {
            type: ARRAY,
            value: []
        };
    }

    return {
        type: ARRAY,
        value: array.value.filter(it => {
            const argMatchers: EvalValue[][] = [];

            if (typeof it === 'string') {
                if (parseColor(it)) {
                    argMatchers.push([{
                        type: COLOR,
                        value: it
                    }]);
                }
                if (safeCheckUrl(it)) {
                    argMatchers.push([{
                        type: URL,
                        value: it
                    }]);
                }
                argMatchers.push([{
                    type: STRING,
                    value: it
                }]);
            } else if (typeof it === 'number') {
                if (Math.round(it) === it) {
                    checkIntegerOverflow(ctx, it);
                    argMatchers.push([{
                        type: INTEGER,
                        value: toBigInt(it)
                    }]);
                }
                argMatchers.push([{
                    type: NUMBER,
                    value: it
                }]);
            } else if (typeof it === 'bigint') {
                checkIntegerOverflow(ctx, it);
                argMatchers.push([{
                    type: INTEGER,
                    value: it
                }]);
            } else if (Array.isArray(it)) {
                argMatchers.push([{
                    type: ARRAY,
                    value: it
                }]);
            } else if (typeof it === 'object') {
                if (it === null) {
                    throw new Error('Incorrect value type: Null');
                }
                argMatchers.push([{
                    type: DICT,
                    value: it
                }]);
            } else if (typeof it === 'boolean') {
                argMatchers.push([{
                    type: BOOLEAN,
                    value: it ? 1 : 0
                }]);
            } else {
                throw new Error(`Incorrect value type: ${typeToString(typeof it)}`);
            }

            let fnMatch: FuncMatch = {
                type: 'missing'
            };
            for (const matchItem of argMatchers) {
                fnMatch = findBestMatchedFuncList(fn.value, matchItem);
                if ('func' in fnMatch) {
                    break;
                }
            }

            let selectedFn: Func;
            if ('func' in fnMatch) {
                selectedFn = fnMatch.func;
            } else {
                const selectedFn = fn.value[0];
                logFunctionMatchError(selectedFn.name || 'Function', argMatchers[0], fnMatch, true);
            }

            const argType = selectedFn.args[0];
            const value = convertJsValueToDivKit(
                ctx,
                it,
                typeof argType === 'string' ? argType : argType.type
            );
            const res = selectedFn.cb(ctx, value);

            if (res.type !== BOOLEAN) {
                throw new Error('Function must return boolean value.');
            }

            return res.value;
        })
    };
}

export function registerArray(): void {
    registerFunc('getArrayString', [
        ARRAY,
        INTEGER
    ], getArrayString);
    registerFunc('getStringFromArray', [
        ARRAY,
        INTEGER
    ], getArrayString);

    registerFunc('getArrayNumber', [
        ARRAY,
        INTEGER
    ], getArrayNumber);
    registerFunc('getNumberFromArray', [
        ARRAY,
        INTEGER
    ], getArrayNumber);

    registerFunc('getArrayInteger', [
        ARRAY,
        INTEGER
    ], getArrayInteger);
    registerFunc('getIntegerFromArray', [
        ARRAY,
        INTEGER
    ], getArrayInteger);

    registerFunc('getArrayBoolean', [
        ARRAY,
        INTEGER
    ], getArrayBoolean);
    registerFunc('getBooleanFromArray', [
        ARRAY,
        INTEGER
    ], getArrayBoolean);

    registerFunc('getArrayColor', [
        ARRAY,
        INTEGER
    ], getArrayColor);
    registerFunc('getColorFromArray', [
        ARRAY,
        INTEGER
    ], getArrayColor);

    registerFunc('getArrayUrl', [
        ARRAY,
        INTEGER
    ], getArrayUrl);
    registerFunc('getUrlFromArray', [
        ARRAY,
        INTEGER
    ], getArrayUrl);

    registerFunc('getArrayFromArray', [
        ARRAY,
        INTEGER
    ], getArrayArray);

    registerFunc('getDictFromArray', [
        ARRAY,
        INTEGER
    ], getArrayDict);

    registerFunc('getArrayOptString', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptString);
    registerFunc('getOptStringFromArray', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptString);

    registerFunc('getArrayOptNumber', [
        ARRAY,
        INTEGER,
        NUMBER
    ], getArrayOptNumber);
    registerFunc('getOptNumberFromArray', [
        ARRAY,
        INTEGER,
        NUMBER
    ], getArrayOptNumber);

    registerFunc('getArrayOptInteger', [
        ARRAY,
        INTEGER,
        INTEGER
    ], getArrayOptInteger);
    registerFunc('getOptIntegerFromArray', [
        ARRAY,
        INTEGER,
        INTEGER
    ], getArrayOptInteger);

    registerFunc('getArrayOptBoolean', [
        ARRAY,
        INTEGER,
        BOOLEAN
    ], getArrayOptBoolean);
    registerFunc('getOptBooleanFromArray', [
        ARRAY,
        INTEGER,
        BOOLEAN
    ], getArrayOptBoolean);

    registerFunc('getArrayOptColor', [
        ARRAY,
        INTEGER,
        COLOR
    ], getArrayOptColor);
    registerFunc('getOptColorFromArray', [
        ARRAY,
        INTEGER,
        COLOR
    ], getArrayOptColor);
    registerFunc('getArrayOptColor', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptColor);
    registerFunc('getOptColorFromArray', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptColor);

    registerFunc('getArrayOptUrl', [
        ARRAY,
        INTEGER,
        URL
    ], getArrayOptUrl);
    registerFunc('getOptUrlFromArray', [
        ARRAY,
        INTEGER,
        URL
    ], getArrayOptUrl);
    registerFunc('getArrayOptUrl', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptUrl);
    registerFunc('getOptUrlFromArray', [
        ARRAY,
        INTEGER,
        STRING
    ], getArrayOptUrl);

    registerFunc('getOptArrayFromArray', [
        ARRAY,
        INTEGER
    ], getArrayOptArray);

    registerFunc('getOptDictFromArray', [
        ARRAY,
        INTEGER
    ], getArrayOptDict);

    registerFunc('len', [
        ARRAY
    ], len);

    registerMethod('getString', [ARRAY, INTEGER], getArrayString);
    registerMethod('getInteger', [ARRAY, INTEGER], getArrayInteger);
    registerMethod('getNumber', [ARRAY, INTEGER], getArrayNumber);
    registerMethod('getBoolean', [ARRAY, INTEGER], getArrayBoolean);
    registerMethod('getUrl', [ARRAY, INTEGER], getArrayUrl);
    registerMethod('getColor', [ARRAY, INTEGER], getArrayColor);
    registerMethod('getArray', [ARRAY, INTEGER], getArrayArray);
    registerMethod('getDict', [ARRAY, INTEGER], getArrayDict);
    registerMethod('isEmpty', [ARRAY], isEmpty);
    registerMethod('filter', [ARRAY, FUNCTION], filter);
}
