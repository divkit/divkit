import { toBigInt } from '../bigint';
import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import type { ArrayValue, BooleanValue, ColorValue, EvalContext, EvalTypes, EvalTypesWithoutDatetime, EvalValue, IntegerValue, NumberValue, StringValue, UrlValue } from '../eval';
import { convertJsValueToDivKit, transformColorValue } from '../utils';
import { registerFunc } from './funcs';

function arrayGetter(evalType: EvalTypesWithoutDatetime) {
    return (ctx: EvalContext, array: ArrayValue, index: IntegerValue): EvalValue => {
        if (index.value < 0 || index.value >= array.value.length) {
            throw new Error(`Requested index (${index.value}) out of bounds array size (${array.value.length}).`);
        }
        const val = array.value[Number(index.value)];

        return convertJsValueToDivKit(ctx, val, evalType);
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

const getArrayString = arrayGetter(STRING);
const getArrayNumber = arrayGetter(NUMBER);
const getArrayInteger = arrayGetter(INTEGER);
const getArrayBoolean = arrayGetter(BOOLEAN);
const getArrayColor = arrayGetter(COLOR);
const getArrayUrl = arrayGetter(URL);
const getArrayArray = arrayGetter(ARRAY);
const getArrayDict = arrayGetter(DICT);

const getArrayOptString = optWrapper<StringValue>(getArrayString, STRING);
const getArrayOptNumber = optWrapper<NumberValue>(getArrayNumber, NUMBER);
const getArrayOptInteger = optWrapper<IntegerValue>(getArrayInteger, INTEGER);
const getArrayOptBoolean = optWrapper<BooleanValue>(getArrayBoolean, BOOLEAN);
const getArrayOptColor = optWrapper<ColorValue>(getArrayColor, COLOR);
const getArrayOptUrl = optWrapper<UrlValue>(getArrayUrl, URL);

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
