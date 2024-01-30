import { toBigInt } from '../bigint';
import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import type { ArrayValue, BooleanValue, ColorValue, EvalContext, EvalTypes, EvalValue, IntegerValue, NumberValue, StringValue, UrlValue } from '../eval';
import { checkIntegerOverflow, transformColorValue } from '../utils';
import { registerFunc } from './funcs';

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
                    type = 'array';
                } else if (val === null) {
                    type = 'null';
                } else {
                    type = 'dict';
                }
            }
            throw new Error(`Incorrect value type: expected "${runtimeType}", got "${type}".`);
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
}
