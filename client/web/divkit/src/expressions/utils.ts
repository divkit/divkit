import type { EvalContext, EvalTypesWithoutDatetime, EvalValue, EvalValueBase, IntegerValue, NumberValue } from './eval';
import type { Node, Variable } from './ast';
import type { VariablesMap } from './eval';
import { walk } from './walk';
import { parseColor, type ParsedColor } from '../utils/correctColor';
import { padLeft } from '../utils/padLeft';
import { MAX_INT, MIN_INT, toBigInt } from './bigint';
import { NUMBER } from './const';

export function valToInternal(val: EvalValue): EvalValue {
    if (val.type === 'url' || val.type === 'color') {
        return {
            type: 'string',
            value: val.value
        };
    }

    return val;
}

export function dateToString(date: Date): string {
    return date
        .toISOString()
        .replace('T', ' ')
        .replace(/\.\d{3}Z$/, '');
}

export function valToString(val: EvalValue, stringifyComplex: boolean): string {
    if (val.type === 'string') {
        return val.value;
    } else if (val.type === 'integer') {
        return String(val.value);
    } else if (val.type === 'number') {
        let res = String(val.value);

        if (!res.includes('.')) {
            if (res.includes('e')) {
                res = res.replace('e', '.0e');
            } else {
                res += '.0';
            }
        }

        res = res.replace(/e\+?/i, 'E');

        return res;
    } else if (val.type === 'boolean') {
        return val.value ? 'true' : 'false';
    } else if (val.type === 'datetime') {
        return dateToString(val.value);
    } else if (val.type === 'color') {
        return stringifyColor(safeConvertColor(val.value));
    } else if (val.type === 'url') {
        return val.value;
    } else if ((val.type === 'dict' || val.type === 'array') && stringifyComplex) {
        return JSON.stringify(val.value);
    } else if (val.type === 'dict') {
        return '<dict>';
    } else if (val.type === 'array') {
        return '<array>';
    }

    // For purpose when new eval value types will be added
    throw new Error(`Unexpected type ${(val as EvalValueBase).type}`);
}

export function valToPreview(val: EvalValue): string {
    let res = valToString(val, false);

    if (val.type === 'string') {
        res = "'" +
            res
                .replace(/\\/g, '\\\\')
                .replace(/'/g, '\\\'') +
            "'";
    }

    return res;
}

export function typeToString(type: string): string {
    if (type === 'datetime') {
        return 'DateTime';
    }
    return type.charAt(0).toUpperCase() + type.substring(1);
}

export function roundInteger(_ctx: EvalContext, val: bigint): bigint {
    return toBigInt(val);
}

export function checkIntegerOverflow(_ctx: EvalContext, val: number | bigint): void {
    if (val < MIN_INT || val > MAX_INT) {
        throw new Error('Integer overflow.');
    }
}

export function gatherVarsFromAst(ast: Node): string[] {
    const res = new Set<string>();

    walk(ast, {
        Variable(node: Variable): void {
            res.add(node.id.name);
        }
    });

    return [...res];
}

export function evalError(msg: string, details: string): never {
    throw new Error(`Failed to evaluate [${msg}]. ${details}`);
}

export function containsUnsetVariables(ast: Node, variables: VariablesMap): boolean {
    let result = false;

    walk(ast, {
        Variable(node: Variable): void {
            if (!variables.has(node.id.name)) {
                result = true;
            }
        }
    });

    return result;
}

export function safeConvertColor(color: string): ParsedColor {
    const res = parseColor(color);

    if (res) {
        return res;
    }

    throw new Error('Unable to convert value to Color, expected format #AARRGGBB.');
}

export function stringifyColor(color: ParsedColor): string {
    return `#${[color.a, color.r, color.g, color.b].map(it => {
        if (it < 0 || it > 255) {
            throw new Error('Value out of range 0..1.');
        }

        return padLeft(Math.round(it).toString(16), 2);
    }).join('').toUpperCase()}`;
}

export function transformColorValue(color: string): string {
    return stringifyColor(safeConvertColor(color));
}

export function integerToNumber(integerValue: IntegerValue): NumberValue {
    return {
        type: NUMBER,
        value: Number(integerValue.value)
    };
}

const EVAL_TYPE_TO_JS_TYPE = {
    string: 'string',
    number: 'number',
    integer: 'number',
    boolean: 'boolean',
    color: 'string',
    url: 'string',
    array: 'array',
    dict: 'object'
};
export function convertJsValueToDivKit(ctx: EvalContext, val: unknown, evalType: EvalTypesWithoutDatetime): EvalValue {
    const jsType = EVAL_TYPE_TO_JS_TYPE[evalType];

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
        throw new Error(`Incorrect value type: expected ${typeToString(evalType)}, got ${typeToString(type)}.`);
    }
    if (jsType === 'number' && evalType === 'integer') {
        checkIntegerOverflow(ctx, val as number);
        try {
            val = toBigInt(val as number);
        } catch (_err) {
            throw new Error('Cannot convert value to integer.');
        }
    }
    if (jsType === 'string' && evalType === 'color') {
        val = transformColorValue(val as string);
    }

    return {
        type: evalType,
        value: val
    } as EvalValue;
}
