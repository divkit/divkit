import type { EvalValue, IntegerValue, StringValue } from '../eval';
import type { VariablesMap } from '../eval';
import { registerFunc } from './funcs';
import { BOOLEAN, INTEGER, STRING } from '../const';
import { escapeRegExp } from '../../utils/escapeRegExp';
import { valToString } from '../utils';

function len(_vars: VariablesMap, arg: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: arg.value.length
    };
}

function contains(_vars: VariablesMap, wholeStr: StringValue, partStr: StringValue): EvalValue {
    return {
        type: BOOLEAN,
        value: wholeStr.value.includes(partStr.value) ? 1 : 0
    };
}

function substring(_vars: VariablesMap, str: StringValue, start: IntegerValue, end: IntegerValue): EvalValue {
    if (end.value < start.value) {
        throw new Error('Indexes should be in ascending order.');
    }

    if (
        start.value < 0 || start.value > str.value.length ||
        end.value < 0 || end.value > str.value.length
    ) {
        throw new Error('Indexes are out of bounds.');
    }

    return {
        type: STRING,
        value: str.value.substring(start.value, end.value)
    };
}

function replaceAll(_vars: VariablesMap, str: StringValue, what: StringValue, replacer: StringValue): EvalValue {
    let res: string;

    if (what.value) {
        res = str.value.replace(new RegExp(escapeRegExp(what.value), 'g'), replacer.value);
    } else {
        // empty str
        res = str.value;
    }

    return {
        type: STRING,
        value: res
    };
}

function index(_vars: VariablesMap, str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.indexOf(what.value)
    };
}

function lastIndex(_vars: VariablesMap, str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.lastIndexOf(what.value)
    };
}

function trim(_vars: VariablesMap, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.trim()
    };
}

function trimLeft(_vars: VariablesMap, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/^\s+/, '')
    };
}

function trimRight(_vars: VariablesMap, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/\s+$/, '')
    };
}

function toUpperCase(_vars: VariablesMap, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toUpperCase()
    };
}

function toLowerCase(_vars: VariablesMap, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toLowerCase()
    };
}

function calcPad(val: StringValue | IntegerValue, len: IntegerValue, pad: StringValue): string {
    if (!pad.value.length) {
        return '';
    }

    let part = '';
    const str = val.type === STRING ? val.value : valToString(val);

    while (part.length + str.length < len.value) {
        part += pad.value;
    }
    if (part.length > 0 && part.length + str.length > len.value) {
        part = part.substring(0, len.value - str.length);
    }

    return part;
}

function padStart(
    _vars: VariablesMap,
    val: StringValue | IntegerValue,
    len: IntegerValue,
    pad: StringValue
): EvalValue {
    const prefix = calcPad(val, len, pad);

    return {
        type: STRING,
        value: prefix + valToString(val)
    };
}

function padEnd(
    _vars: VariablesMap,
    val: StringValue | IntegerValue,
    len: IntegerValue,
    pad: StringValue
): EvalValue {
    const suffix = calcPad(val, len, pad);

    return {
        type: STRING,
        value: valToString(val) + suffix
    };
}

export function registerStrings(): void {
    registerFunc('len', [STRING], len);
    registerFunc('contains', [STRING, STRING], contains);
    registerFunc('substring', [STRING, INTEGER, INTEGER], substring);
    registerFunc('replaceAll', [STRING, STRING, STRING], replaceAll);
    registerFunc('index', [STRING, STRING], index);
    registerFunc('lastIndex', [STRING, STRING], lastIndex);
    registerFunc('trim', [STRING], trim);
    registerFunc('trimLeft', [STRING], trimLeft);
    registerFunc('trimRight', [STRING], trimRight);
    registerFunc('toUpperCase', [STRING], toUpperCase);
    registerFunc('toLowerCase', [STRING], toLowerCase);
    registerFunc('padStart', [STRING, INTEGER, STRING], padStart);
    registerFunc('padStart', [INTEGER, INTEGER, STRING], padStart);
    registerFunc('padEnd', [STRING, INTEGER, STRING], padEnd);
    registerFunc('padEnd', [INTEGER, INTEGER, STRING], padEnd);
}
