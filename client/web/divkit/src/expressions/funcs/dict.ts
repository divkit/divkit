import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import type { BooleanValue, DictValue, EvalContext, EvalTypes, EvalTypesWithoutDatetime, EvalValue, IntegerValue, NumberValue, StringValue } from '../eval';
import { convertJsValueToDivKit, transformColorValue } from '../utils';
import { registerFunc, registerMethod } from './funcs';

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
        if (path.length === 0) {
            throw new Error('Non empty argument list is required.');
        }

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

function containsKey(_ext: EvalContext, dict: DictValue, key: StringValue): BooleanValue {
    return {
        type: BOOLEAN,
        value: key.value in dict.value ? 1 : 0
    };
}

function isEmpty(_ext: EvalContext, dict: DictValue): BooleanValue {
    return {
        type: BOOLEAN,
        value: Object.keys(dict.value).length ? 0 : 1
    };
}

export function registerDict(): void {
    const STRING_VARARG = {
        type: STRING,
        isVararg: true
    } as const;

    registerFunc('getDictString', [DICT, STRING_VARARG], getDictString);
    registerFunc('getStringFromDict', [DICT, STRING_VARARG], getDictString);

    registerFunc('getDictNumber', [DICT, STRING_VARARG], getDictNumber);
    registerFunc('getNumberFromDict', [DICT, STRING_VARARG], getDictNumber);

    registerFunc('getDictInteger', [DICT, STRING_VARARG], getDictInteger);
    registerFunc('getIntegerFromDict', [DICT, STRING_VARARG], getDictInteger);

    registerFunc('getDictBoolean', [DICT, STRING_VARARG], getDictBoolean);
    registerFunc('getBooleanFromDict', [DICT, STRING_VARARG], getDictBoolean);

    registerFunc('getDictColor', [DICT, STRING_VARARG], getDictColor);
    registerFunc('getColorFromDict', [DICT, STRING_VARARG], getDictColor);

    registerFunc('getDictUrl', [DICT, STRING_VARARG], getDictUrl);
    registerFunc('getUrlFromDict', [DICT, STRING_VARARG], getDictUrl);

    registerFunc('getDictOptString', [STRING, DICT, STRING_VARARG], getDictOptString);
    registerFunc('getOptStringFromDict', [STRING, DICT, STRING_VARARG], getDictOptString);

    registerFunc('getDictOptNumber', [NUMBER, DICT, STRING_VARARG], getDictOptNumber);
    registerFunc('getOptNumberFromDict', [NUMBER, DICT, STRING_VARARG], getDictOptNumber);

    registerFunc('getDictOptInteger', [INTEGER, DICT, STRING_VARARG], getDictOptInteger);
    registerFunc('getOptIntegerFromDict', [INTEGER, DICT, STRING_VARARG], getDictOptInteger);

    registerFunc('getDictOptBoolean', [BOOLEAN, DICT, STRING_VARARG], getDictOptBoolean);
    registerFunc('getOptBooleanFromDict', [BOOLEAN, DICT, STRING_VARARG], getDictOptBoolean);

    registerFunc('getDictOptColor', [COLOR, DICT, STRING_VARARG], getDictOptColor);
    registerFunc('getOptColorFromDict', [COLOR, DICT, STRING_VARARG], getDictOptColor);

    registerFunc('getDictOptColor', [STRING, DICT, STRING_VARARG], getDictOptColor);
    registerFunc('getOptColorFromDict', [STRING, DICT, STRING_VARARG], getDictOptColor);

    registerFunc('getDictOptUrl', [STRING, DICT, STRING_VARARG], getDictOptUrl);
    registerFunc('getOptUrlFromDict', [STRING, DICT, STRING_VARARG], getDictOptUrl);

    registerFunc('getDictOptUrl', [URL, DICT, STRING_VARARG], getDictOptUrl);
    registerFunc('getOptUrlFromDict', [URL, DICT, STRING_VARARG], getDictOptUrl);

    registerFunc('getDictFromDict', [DICT, STRING_VARARG], getDictDict);

    registerFunc('getArrayFromDict', [DICT, STRING_VARARG], getDictArray);

    registerFunc('getOptArrayFromDict', [DICT, STRING_VARARG], getDictOptArray);

    registerFunc('getOptDictFromDict', [DICT, STRING_VARARG], getDictOptDict);

    registerMethod('getString', [DICT, STRING_VARARG], getDictString);
    registerMethod('getBoolean', [DICT, STRING_VARARG], getDictBoolean);
    registerMethod('getInteger', [DICT, STRING_VARARG], getDictInteger);
    registerMethod('getNumber', [DICT, STRING_VARARG], getDictNumber);
    registerMethod('getUrl', [DICT, STRING_VARARG], getDictUrl);
    registerMethod('getColor', [DICT, STRING_VARARG], getDictColor);
    registerMethod('getArray', [DICT, STRING_VARARG], getDictArray);
    registerMethod('getDict', [DICT, STRING_VARARG], getDictDict);
    registerMethod('containsKey', [DICT, STRING], containsKey);
    registerMethod('isEmpty', [DICT], isEmpty);
}
