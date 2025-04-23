import type {
    ArrayValue,
    BooleanValue,
    ColorValue,
    DictValue,
    EvalContext,
    EvalValue,
    IntegerValue,
    NumberValue,
    StringValue,
    UrlValue
} from '../eval';
import type { VariableType, VariableValue } from '../variable';
import { registerFunc, registerMethod } from './funcs';
import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import { transformColorValue, valToString } from '../utils';
import { MAX_INT, MIN_INT, toBigInt } from '../bigint';

function toString(
    _ctx: EvalContext,
    arg: IntegerValue | NumberValue | BooleanValue | ColorValue | UrlValue | StringValue | ArrayValue | DictValue
): EvalValue {
    return {
        type: STRING,
        value: valToString(arg, true)
    };
}


function toNumber(_ctx: EvalContext, arg: IntegerValue | StringValue): EvalValue {
    const num = Number(arg.value);

    if (Number.isNaN(num) || !Number.isFinite(num)) {
        throw new Error('Unable to convert value to Number.');
    }
    if (arg.value === '') {
        throw new Error('Unable to convert value to Number.');
    }

    return {
        type: NUMBER,
        value: num
    };
}


function toIntegerNumber(_ctx: EvalContext, arg: NumberValue): EvalValue {
    if (arg.value > MAX_INT || arg.value < MIN_INT) {
        throw new Error('Unable to convert value to Integer.');
    }

    const num = arg.value - (arg.value % 1);

    return {
        type: INTEGER,
        value: toBigInt(num)
    };
}

function toIntegerString(_ctx: EvalContext, arg: StringValue): EvalValue {
    let num: bigint;

    try {
        num = toBigInt(arg.value);
    } catch (err) {
        throw new Error('Unable to convert value to Integer.');
    }

    return {
        type: INTEGER,
        value: num
    };
}

function toIntegerBoolean(_ctx: EvalContext, arg: BooleanValue): EvalValue {
    return {
        type: INTEGER,
        value: toBigInt(arg.value ? 1 : 0)
    };
}


function toBooleanInteger(_ctx: EvalContext, arg: IntegerValue): EvalValue {
    const intVal = Number(arg.value);
    if (intVal !== 1 && intVal !== 0) {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: intVal
    };
}

function toBooleanString(_ctx: EvalContext, arg: StringValue): EvalValue {
    if (arg.value !== 'true' && arg.value !== 'false') {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: arg.value === 'true' ? 1 : 0
    };
}

function toColor(_ctx: EvalContext, arg: StringValue): EvalValue {
    return {
        type: COLOR,
        value: transformColorValue(arg.value)
    };
}

function toUrl(_ctx: EvalContext, arg: StringValue): EvalValue {
    return {
        type: URL,
        value: arg.value
    };
}

function encodeUri(_ctx: EvalContext, str: StringValue): EvalValue {
    try {
        return {
            type: STRING,
            value: encodeURIComponent(str.value)
        };
    } catch (_err) {
        throw new Error('Unable to encodeUri string.');
    }
}

function decodeUri(_ctx: EvalContext, str: StringValue): EvalValue {
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
    ctx: EvalContext,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue,
    type: VariableType
): EvalValue {
    const variable = ctx.variables.get(varName.value);
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
    ctx: EvalContext,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
): EvalValue {
    return getValueForced(ctx, varName, fallback, fallback.type);
}

function getColorValue(
    ctx: EvalContext,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
) {
    return getValueForced(ctx, varName, fallback, 'color');
}

function getUrlValue(
    ctx: EvalContext,
    varName: StringValue,
    fallback: IntegerValue | NumberValue | StringValue | BooleanValue | UrlValue | ColorValue
) {
    return getValueForced(ctx, varName, fallback, 'url');
}

export function registerStd(): void {
    registerFunc('toString', [INTEGER], toString);
    registerFunc('toString', [NUMBER], toString);
    registerFunc('toString', [BOOLEAN], toString);
    registerFunc('toString', [COLOR], toString);
    registerFunc('toString', [URL], toString);
    registerFunc('toString', [STRING], toString);
    registerFunc('toString', [ARRAY], toString);
    registerFunc('toString', [DICT], toString);

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

    registerMethod('toString', [INTEGER], toString);
    registerMethod('toString', [NUMBER], toString);
    registerMethod('toString', [BOOLEAN], toString);
    registerMethod('toString', [COLOR], toString);
    registerMethod('toString', [URL], toString);
    registerMethod('toString', [STRING], toString);
    registerMethod('toString', [ARRAY], toString);
    registerMethod('toString', [DICT], toString);
}
