import type { BooleanValue, ColorValue, EvalValue, IntegerValue, NumberValue, StringValue, UrlValue } from '../eval';
import { registerFunc } from './funcs';
import { BOOLEAN, COLOR, INTEGER, MAX_INT, MIN_INT, NUMBER, STRING, URL } from '../const';
import { valToString } from '../utils';

function toString(arg: IntegerValue | NumberValue | BooleanValue | ColorValue | UrlValue): EvalValue {
    return {
        type: STRING,
        value: valToString(arg)
    };
}


function toNumber(arg: IntegerValue | StringValue): EvalValue {
    const num = Number(arg.value);

    if (isNaN(num) || !Number.isFinite(num)) {
        throw new Error('Unable to convert value to Number.');
    }

    return {
        type: NUMBER,
        value: num
    };
}


function toIntegerNumber(arg: NumberValue): EvalValue {
    if (arg.value > MAX_INT || arg.value < MIN_INT) {
        throw new Error('Unable to convert value to Integer.');
    }

    const num = arg.value - (arg.value % 1);

    return {
        type: INTEGER,
        value: num
    };
}

function toIntegerString(arg: StringValue): EvalValue {
    const num = Number(arg.value);

    if (isNaN(num) || num % 1 !== 0 || num > MAX_INT || num < MIN_INT) {
        throw new Error('Unable to convert value to Integer.');
    }

    return {
        type: INTEGER,
        value: num
    };
}

function toIntegerBoolean(arg: BooleanValue): EvalValue {
    return {
        type: INTEGER,
        value: arg.value ? 1 : 0
    };
}


function toBooleanInteger(arg: IntegerValue): EvalValue {
    if (arg.value !== 1 && arg.value !== 0) {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: arg.value
    };
}

function toBooleanString(arg: StringValue): EvalValue {
    if (arg.value !== 'true' && arg.value !== 'false') {
        throw new Error('Unable to convert value to Boolean.');
    }

    return {
        type: BOOLEAN,
        value: arg.value === 'true' ? 1 : 0
    };
}

function encodeUri(str: StringValue): EvalValue {
    try {
        return {
            type: STRING,
            value: encodeURIComponent(str.value)
        };
    } catch (_err) {
        throw new Error('Unable to encodeUri string.');
    }
}

function decodeUri(str: StringValue): EvalValue {
    try {
        return {
            type: STRING,
            value: decodeURIComponent(str.value)
        };
    } catch (_err) {
        throw new Error('Unable to decodeUri string.');
    }
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

    registerFunc('encodeUri', [STRING], encodeUri);
    registerFunc('decodeUri', [STRING], decodeUri);
}
