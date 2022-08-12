import type { EvalValue, IntegerValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { BOOLEAN, INTEGER, STRING } from '../const';
import { escapeRegExp } from '../../utils/escapeRegExp';

function len(arg: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: arg.value.length
    };
}

function contains(wholeStr: StringValue, partStr: StringValue): EvalValue {
    return {
        type: BOOLEAN,
        value: wholeStr.value.includes(partStr.value) ? 1 : 0
    };
}

function substring(str: StringValue, start: IntegerValue, end: IntegerValue): EvalValue {
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

function replaceAll(str: StringValue, what: StringValue, replacer: StringValue): EvalValue {
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

function index(str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.indexOf(what.value)
    };
}

function lastIndex(str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.lastIndexOf(what.value)
    };
}

function trim(str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.trim()
    };
}

function trimLeft(str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/^\s+/, '')
    };
}

function trimRight(str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/\s+$/, '')
    };
}

function toUpperCase(str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toUpperCase()
    };
}

function toLowerCase(str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toLowerCase()
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
}
