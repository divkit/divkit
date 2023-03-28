import type { EvalContext, EvalValue, IntegerValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { BOOLEAN, INTEGER, STRING } from '../const';
import { escapeRegExp } from '../../utils/escapeRegExp';
import { valToString } from '../utils';
import { wrapError } from '../../utils/wrapError';

function len(_ctx: EvalContext, arg: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: arg.value.length
    };
}

function contains(_ctx: EvalContext, wholeStr: StringValue, partStr: StringValue): EvalValue {
    return {
        type: BOOLEAN,
        value: wholeStr.value.includes(partStr.value) ? 1 : 0
    };
}

function substring(_ctx: EvalContext, str: StringValue, start: IntegerValue, end: IntegerValue): EvalValue {
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
        value: str.value.substring(Number(start.value), Number(end.value))
    };
}

function replaceAll(_ctx: EvalContext, str: StringValue, what: StringValue, replacer: StringValue): EvalValue {
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

function index(_ctx: EvalContext, str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.indexOf(what.value)
    };
}

function lastIndex(_ctx: EvalContext, str: StringValue, what: StringValue): EvalValue {
    return {
        type: INTEGER,
        value: str.value.lastIndexOf(what.value)
    };
}

function trim(_ctx: EvalContext, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.trim()
    };
}

function trimLeft(_ctx: EvalContext, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/^\s+/, '')
    };
}

function trimRight(_ctx: EvalContext, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.replace(/\s+$/, '')
    };
}

function toUpperCase(_ctx: EvalContext, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toUpperCase()
    };
}

function toLowerCase(_ctx: EvalContext, str: StringValue): EvalValue {
    return {
        type: STRING,
        value: str.value.toLowerCase()
    };
}

function calcPad(
    ctx: EvalContext,
    val: StringValue | IntegerValue,
    len: IntegerValue,
    pad: StringValue
): string {
    if (!pad.value.length) {
        ctx.warnings.push(wrapError(new Error('String for padding is empty.'), {
            level: 'warn'
        }));
        return '';
    }

    let part = '';
    const str = val.type === STRING ? val.value : valToString(val);

    while (part.length + str.length < len.value) {
        part += pad.value;
    }
    if (part.length > 0 && part.length + str.length > len.value) {
        part = part.substring(0, Number(len.value) - Number(str.length));
    }

    return part;
}

function padStart(
    ctx: EvalContext,
    val: StringValue | IntegerValue,
    len: IntegerValue,
    pad: StringValue
): EvalValue {
    const prefix = calcPad(ctx, val, len, pad);

    return {
        type: STRING,
        value: prefix + valToString(val)
    };
}

function padEnd(
    ctx: EvalContext,
    val: StringValue | IntegerValue,
    len: IntegerValue,
    pad: StringValue
): EvalValue {
    const suffix = calcPad(ctx, val, len, pad);

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
