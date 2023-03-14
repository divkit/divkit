import type { BooleanValue, ColorValue, EvalValue, IntegerValue, NumberValue, StringValue, UrlValue } from '../eval';
import type { VariablesMap } from '../eval';
import type { VariableType, VariableValue } from '../variable';
import { registerFunc } from './funcs';
import { BOOLEAN, COLOR, INTEGER, MAX_INT, MIN_INT, NUMBER, STRING, URL } from '../const';
import { transformColorValue, valToString } from '../utils';

function toString(
    _vars: VariablesMap,
    arg: IntegerValue | NumberValue | BooleanValue | ColorValue | UrlValue
): EvalValue {
    return {
        type: STRING,
        value: valToString(arg)
    };
}


function toNumber(_vars: VariablesMap, arg: IntegerValue | StringValue): EvalValue {
    const num = Number(arg.value);

    if (isNaN(num) || !Number.isFinite(num)) {
        throw new Error('Unable to convert value to Number.');
    }

    return {
        type: NUMBER,
        value: num
    };
}


function toIntegerNumber(_vars: VariablesMap, arg: NumberValue): EvalValue {
    if (arg.value > MAX_INT || arg.value < MIN_INT) {
        throw new Error('Unable to convert value to Integer.');
    }

    const num = arg.value - (arg.value % 1);

    return {
        type: INTEGER,
        value: num
    };
}

function toIntegerString(_vars: VariablesMap, arg: StringValue): EvalValue {
    const num = Number(arg.value);

    if (isNaN(num) || num % 1 !== 0 || num > MAX_INT || num < MIN_INT) {
        throw new Error('Unable to convert value to Integer.');
    }

    return {
        type: INTEGER,
        value: num
    };
}

function toIntegerBoolean(_vars: VariablesMap, arg: BooleanValue): EvalValue {
    return {
        type: INTEGER,
        value: arg.value ? 1 : 0
    };
}


function toBooleanInteger(_vars: VariablesMap, arg: IntegerValue): EvalValue {
    if (arg.value !== 1 && arg.value !== 0) {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: arg.value
    };
}

function toBooleanString(_vars: VariablesMap, arg: StringValue): EvalValue {
    if (arg.value !== 'true' && arg.value !== 'false') {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: arg.value === 'true' ? 1 : 0
    };
}

function toColor(_vars: VariablesMap, arg: StringValue): EvalValue {
    return {
        type: COLOR,
        value: transformColorValue(arg.value)
    };
}

function toUrl(_vars: VariablesMap, arg: StringValue): EvalValue {
    return {
        type: URL,
        value: arg.value
    };
}

function encodeUri(_vars: VariablesMap, str: StringValue): EvalValue {
    try {
        return {
            type: STRING,
            value: encodeURIComponent(str.value)
        };
    } catch (_err) {
        throw new Error('Unable to encodeUri string.');
    }
}

function decodeUri(_vars: VariablesMap, str: StringValue): EvalValue {
    try {
        return {
            type: STRING,
            value: decodeURIComponent(str.value)
        };
    } catch (_err) {
        throw new Error('Unable to decodeUri string.');
    }
}

function getValueForced(
    vars: VariablesMap,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue,
    type: VariableType
): EvalValue {
    const variable = vars.get(varName.value);
    let value: VariableValue;

    if (variable && variable.getType() === type) {
        value = variable.getValue();
    } else {
        value = fallback.value;
    }

    if (type === 'color') {
        value = transformColorValue(value as string);
    }

    return {
        type,
        // value is synced with type by params
        value: value as any
    };
}

function getValue(
    vars: VariablesMap,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
): EvalValue {
    return getValueForced(vars, varName, fallback, fallback.type);
}

function getColorValue(
    vars: VariablesMap,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
) {
    return getValueForced(vars, varName, fallback, 'color');
}

function getUrlValue(
    vars: VariablesMap,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
) {
    return getValueForced(vars, varName, fallback, 'url');
}

export function registerStd(): void {
    registerFunc('toString', [INTEGER], toString);
    registerFunc('toString', [NUMBER], toString);
    registerFunc('toString', [BOOLEAN], toString);
    registerFunc('toString', [COLOR], toString);
    registerFunc('toString', [URL], toString);

    registerFunc('toNumber', [INTEGER], toNumber);
    registerFunc('toNumber', [STRING], toNumber);

    registerFunc('toInteger', [NUMBER], toIntegerNumber);
    registerFunc('toInteger', [STRING], toIntegerString);
    registerFunc('toInteger', [BOOLEAN], toIntegerBoolean);

    registerFunc('toBoolean', [INTEGER], toBooleanInteger);
    registerFunc('toBoolean', [STRING], toBooleanString);

    registerFunc('toColor', [STRING], toColor);

    registerFunc('toUrl', [STRING], toUrl);

    registerFunc('encodeUri', [STRING], encodeUri);
    registerFunc('decodeUri', [STRING], decodeUri);

    registerFunc('getIntegerValue', [STRING, INTEGER], getValue);
    registerFunc('getNumberValue', [STRING, NUMBER], getValue);
    registerFunc('getBooleanValue', [STRING, BOOLEAN], getValue);
    registerFunc('getStringValue', [STRING, STRING], getValue);
    registerFunc('getColorValue', [STRING, COLOR], getColorValue);
    registerFunc('getColorValue', [STRING, STRING], getColorValue);
    registerFunc('getUrlValue', [STRING, URL], getUrlValue);
    registerFunc('getUrlValue', [STRING, STRING], getUrlValue);
}
