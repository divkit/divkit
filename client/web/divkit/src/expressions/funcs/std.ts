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
import { checkUrl, transformColorValue, valToString } from '../utils';
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
    checkUrl(arg.value);

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

        if (!ctx.storeUsedVars) {
            ctx.storeUsedVars = new Set();
        }
        ctx.storeUsedVars.add(variable);
    } else {
        value = fallback.value;
    }

    if (type === 'color') {
        value = transformColorValue(value as string);
    } else if (type === 'url') {
        checkUrl(value);
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

function firstDiffChar(str0: string, str1: string): string {
    for (let i = 0; i < str1.length; ++i) {
        const char0 = str0.charAt(i);
        const char1 = str1.charAt(i);

        if (char0 !== char1 && char1) {
            return char1;
        }
    }

    return '';
}

const TEST_NUMBER = 1234567890;

function numberFractionDivider(locale?: string): string {
    const formatter0 = new Intl.NumberFormat(locale, {
        maximumFractionDigits: 0
    });
    const formatter1 = new Intl.NumberFormat(locale, {
        minimumFractionDigits: 1
    });

    const str0 = formatter0.format(TEST_NUMBER);
    const str1 = formatter1.format(TEST_NUMBER);

    return firstDiffChar(str0, str1);
}

function numberGroupingDivider(locale?: string): string {
    const formatter0 = new Intl.NumberFormat(locale, {
        useGrouping: false
    });
    const formatter1 = new Intl.NumberFormat(locale, {
        useGrouping: true
    });

    const str0 = formatter0.format(TEST_NUMBER);
    const str1 = formatter1.format(TEST_NUMBER);

    return firstDiffChar(str0, str1);
}

function decimalFormat(
    _ctx: EvalContext,
    arg: IntegerValue | NumberValue,
    format: StringValue,
    locale?: StringValue
): EvalValue {
    const pattern = format.value;
    const patternWithoutGroupping = pattern.replace(/,/g, '');
    if (
        !/^((#+)|(#*0+))(\.0*#*)?$/.test(patternWithoutGroupping) &&
        !/^#*0*\.((0*#*)|(#+))$/.test(patternWithoutGroupping) ||
        /,.*,/.test(pattern) ||
        pattern.indexOf(',') > pattern.indexOf('.') && pattern.indexOf('.') > -1
    ) {
        throw new Error('Incorrect format pattern.');
    }

    const rawParts = pattern.split('.');
    const rawInteger = rawParts[0];
    const rawFraction = rawParts[1] || '';

    const parts = pattern.replace(/[^#0.]/g, '').split('.');
    const integer = parts[0];
    const fraction = parts[1] || '';

    const groupIndex = rawInteger.indexOf(',');
    const digitsInGroup = groupIndex > -1 ? rawInteger.length - groupIndex - 1 : -1;

    if (groupIndex > -1 && digitsInGroup < 1 || rawFraction.indexOf(',') > -1) {
        throw new Error('Incorrect format pattern.');
    }

    try {
        let minimumIntegerDigits = 0;
        while (integer[integer.length - 1 - minimumIntegerDigits] === '0') {
            ++minimumIntegerDigits;
        }
        let minimumFractionDigits = 0;
        while (fraction[minimumFractionDigits] === '0') {
            ++minimumFractionDigits;
        }
        let maximumFractionDigits = minimumFractionDigits;
        while (fraction[maximumFractionDigits] === '#') {
            ++maximumFractionDigits;
        }

        const formatter = new Intl.NumberFormat(locale?.value || undefined, {
            useGrouping: false,
            minimumIntegerDigits: Math.min(Math.max(minimumIntegerDigits, 1), 21),
            minimumFractionDigits: Math.min(Math.max(minimumFractionDigits, 0), 100),
            maximumFractionDigits: Math.min(Math.max(maximumFractionDigits, minimumFractionDigits, 0), 100),
            roundingMode: 'halfEven'
        });

        let result = formatter.format(arg.value);

        if (groupIndex > -1 && digitsInGroup > 0) {
            const groupChar = numberGroupingDivider(locale?.value);
            const fractionChar = numberFractionDivider(locale?.value);

            if (groupChar && fractionChar) {
                const resultParts = result.split(fractionChar);
                const resultInteger = resultParts[0];
                let res = '';
                for (let i = resultInteger.length - 1; i >= 0; --i) {
                    res = resultInteger[i] + res;

                    // eslint-disable-next-line max-depth
                    if (i > 0 && (resultInteger.length - i) % digitsInGroup === 0) {
                        res = groupChar + res;
                    }
                }

                result = res + (resultParts.length > 1 ? fractionChar + resultParts[1] : '');
            }
        }

        if (minimumFractionDigits === 0 && maximumFractionDigits === 0 && pattern.endsWith('.')) {
            // force fraction delimeter on end
            const divider = numberFractionDivider(locale?.value);
            if (divider) {
                result += divider;
            }
        }

        return {
            type: STRING,
            value: result
        };
    } catch (_err) {
        throw new Error('Incorrect or unsupported number format.' + _err + ' ' + locale?.value || undefined);
    }
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

    registerFunc('decimalFormat', [INTEGER, STRING], decimalFormat);
    registerFunc('decimalFormat', [NUMBER, STRING], decimalFormat);
    registerFunc('decimalFormat', [INTEGER, STRING, STRING], decimalFormat);
    registerFunc('decimalFormat', [NUMBER, STRING, STRING], decimalFormat);

    registerMethod('decimalFormat', [INTEGER, STRING], decimalFormat);
    registerMethod('decimalFormat', [NUMBER, STRING], decimalFormat);
    registerMethod('decimalFormat', [INTEGER, STRING, STRING], decimalFormat);
    registerMethod('decimalFormat', [NUMBER, STRING, STRING], decimalFormat);
}
