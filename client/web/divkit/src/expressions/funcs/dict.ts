import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import type { BooleanValue, DictValue, EvalContext, EvalTypes, EvalTypesWithoutDatetime, EvalValue, IntegerValue, NumberValue, StringValue } from '../eval';
import { convertJsValueToDivKit, transformColorValue } from '../utils';
import { registerFunc } from './funcs';

function getProp(obj: object, path: string[]): unknown {
    let current: object = obj;

    for (let i = 0; i < path.length; ++i) {
        if (!current) {
            throw new Error(`Missing property "${path[i]}" in the dict.`);
        }
        const val = current[path[i] as keyof typeof current];
        if (val === undefined) {
            throw new Error(`Missing property "${path[i]}" in the dict.`);
        }
        current = val;
    }

    return current;
}

function dictGetter(evalType: EvalTypesWithoutDatetime) {
    return (ctx: EvalContext, dict: DictValue, ...path: StringValue[]): EvalValue => {
        const val = getProp(dict.value, path.map(it => it.value));

        return convertJsValueToDivKit(ctx, val, evalType);
    };
}

function optWrapper<ValueType extends EvalValue>(
    func: (ctx: EvalContext, dict: DictValue, ...path: StringValue[]) => EvalValue,
    fallbackType: EvalTypes
) {
    return (ctx: EvalContext, fallback: ValueType, dict: DictValue, ...path: StringValue[]) => {
        try {
            return func(ctx, dict, ...path);
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

const getDictString = dictGetter(STRING);
const getDictNumber = dictGetter(NUMBER);
const getDictInteger = dictGetter(INTEGER);
const getDictBoolean = dictGetter(BOOLEAN);
const getDictColor = dictGetter(COLOR);
const getDictUrl = dictGetter(URL);
const getDictArray = dictGetter(ARRAY);
const getDictDict = dictGetter(DICT);

const getDictOptString = optWrapper<StringValue>(getDictString, STRING);
const getDictOptNumber = optWrapper<NumberValue>(getDictNumber, NUMBER);
const getDictOptInteger = optWrapper<IntegerValue>(getDictInteger, INTEGER);
const getDictOptBoolean = optWrapper<BooleanValue>(getDictBoolean, BOOLEAN);
const getDictOptColor = optWrapper<BooleanValue>(getDictColor, COLOR);
const getDictOptUrl = optWrapper<BooleanValue>(getDictUrl, URL);

function getDictOptArray(ctx: EvalContext, dict: DictValue, ...path: StringValue[]): EvalValue {
    try {
        return getDictArray(ctx, dict, ...path);
    } catch (_err) {
        // ignore error
        return {
            type: ARRAY,
            value: []
        } as unknown as EvalValue;
    }
}

function getDictOptDict(ctx: EvalContext, dict: DictValue, ...path: StringValue[]): EvalValue {
    try {
        return getDictDict(ctx, dict, ...path);
    } catch (_err) {
        // ignore error
        return {
            type: DICT,
            value: {}
        } as unknown as EvalValue;
    }
}

export function registerDict(): void {
    registerFunc('getDictString', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictString);
    registerFunc('getStringFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictString);

    registerFunc('getDictNumber', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictNumber);
    registerFunc('getNumberFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictNumber);

    registerFunc('getDictInteger', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictInteger);
    registerFunc('getIntegerFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictInteger);

    registerFunc('getDictBoolean', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictBoolean);
    registerFunc('getBooleanFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictBoolean);

    registerFunc('getDictColor', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictColor);
    registerFunc('getColorFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictColor);

    registerFunc('getDictUrl', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictUrl);
    registerFunc('getUrlFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictUrl);

    registerFunc('getDictOptString', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptString);
    registerFunc('getOptStringFromDict', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptString);

    registerFunc('getDictOptNumber', [
        NUMBER, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptNumber);
    registerFunc('getOptNumberFromDict', [
        NUMBER, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptNumber);

    registerFunc('getDictOptInteger', [
        INTEGER, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptInteger);
    registerFunc('getOptIntegerFromDict', [
        INTEGER, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptInteger);

    registerFunc('getDictOptBoolean', [
        BOOLEAN, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptBoolean);
    registerFunc('getOptBooleanFromDict', [
        BOOLEAN, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptBoolean);

    registerFunc('getDictOptColor', [
        COLOR, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptColor);
    registerFunc('getOptColorFromDict', [
        COLOR, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptColor);

    registerFunc('getDictOptColor', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptColor);
    registerFunc('getOptColorFromDict', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptColor);

    registerFunc('getDictOptUrl', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptUrl);
    registerFunc('getOptUrlFromDict', [
        STRING, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptUrl);

    registerFunc('getDictOptUrl', [
        URL, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptUrl);
    registerFunc('getOptUrlFromDict', [
        URL, DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptUrl);

    registerFunc('getDictFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictDict);

    registerFunc('getArrayFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictArray);

    registerFunc('getOptArrayFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptArray);

    registerFunc('getOptDictFromDict', [
        DICT, {
            type: STRING,
            isVararg: true
        }
    ], getDictOptDict);
}
