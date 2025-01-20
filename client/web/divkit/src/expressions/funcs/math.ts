import type { EvalContext, EvalValue, IntegerValue, NumberValue } from '../eval';
import { registerFunc } from './funcs';
import { INTEGER, MAX_NUMBER, MIN_NUMBER, NUMBER } from '../const';
import { checkIntegerOverflow, roundInteger } from '../utils';
import { absBigInt, bigIntZero, signBigInt, MAX_INT, MIN_INT, toBigInt } from '../bigint';

function divInteger(ctx: EvalContext, arg0: IntegerValue, arg1: IntegerValue): EvalValue {
    if (arg1.value === bigIntZero) {
        throw new Error('Division by zero is not supported.');
    }

    let res = arg0.value / arg1.value;
    res = roundInteger(ctx, res);
    checkIntegerOverflow(ctx, res);

    return {
        type: INTEGER,
        value: res
    };
}

function divNumber(_ctx: EvalContext, arg0: NumberValue, arg1: NumberValue): EvalValue {
    if (arg1.value === 0) {
        throw new Error('Division by zero is not supported.');
    }

    const res = arg0.value / arg1.value;

    return {
        type: NUMBER,
        value: res
    };
}

function modInteger(ctx: EvalContext, arg0: IntegerValue, arg1: IntegerValue): EvalValue {
    if (arg1.value === bigIntZero) {
        throw new Error('Division by zero is not supported.');
    }

    let res = arg0.value % arg1.value;
    res = roundInteger(ctx, res);
    checkIntegerOverflow(ctx, res);

    return {
        type: INTEGER,
        value: res
    };
}

function modNumber(_ctx: EvalContext, arg0: NumberValue, arg1: NumberValue): EvalValue {
    if (arg1.value === 0) {
        throw new Error('Division by zero is not supported.');
    }

    const res = arg0.value % arg1.value;

    return {
        type: NUMBER,
        value: res
    };
}

function mulInteger(ctx: EvalContext, ...args: IntegerValue[]): EvalValue {
    let res = args.length ? args[0].value : bigIntZero;
    for (let i = 1; i < args.length; ++i) {
        res *= args[i].value;
        res = roundInteger(ctx, res);
        checkIntegerOverflow(ctx, res);
    }

    return {
        type: INTEGER,
        value: res
    };
}

function mulNumber(_ctx: EvalContext, ...args: NumberValue[]): EvalValue {
    let res = args.length ? args[0].value : 0;
    for (let i = 1; i < args.length; ++i) {
        res *= args[i].value;
    }

    return {
        type: NUMBER,
        value: res
    };
}

function subInteger(ctx: EvalContext, ...args: IntegerValue[]): EvalValue {
    let res = args.length ? args[0].value : bigIntZero;
    for (let i = 1; i < args.length; ++i) {
        res -= args[i].value;
        res = roundInteger(ctx, res);
        checkIntegerOverflow(ctx, res);
    }

    return {
        type: INTEGER,
        value: res
    };
}

function subNumber(_ctx: EvalContext, ...args: NumberValue[]): EvalValue {
    let res = args.length ? args[0].value : 0;
    for (let i = 1; i < args.length; ++i) {
        res -= args[i].value;
    }

    return {
        type: NUMBER,
        value: res
    };
}

function sumInteger(ctx: EvalContext, ...args: IntegerValue[]): EvalValue {
    let res = bigIntZero;
    for (let i = 0; i < args.length; ++i) {
        res += args[i].value;
        res = roundInteger(ctx, res);
        checkIntegerOverflow(ctx, res);
    }

    return {
        type: INTEGER,
        value: res
    };
}

function sumNumber(_ctx: EvalContext, ...args: NumberValue[]): EvalValue {
    let res = 0;
    for (let i = 0; i < args.length; ++i) {
        res += args[i].value;
    }

    return {
        type: NUMBER,
        value: res
    };
}

function absInteger(ctx: EvalContext, arg: IntegerValue): EvalValue {
    const res = absBigInt(arg.value);

    checkIntegerOverflow(ctx, res);

    return {
        type: arg.type,
        value: res
    };
}

function absNumber(_ctx: EvalContext, arg: NumberValue): EvalValue {
    const res = Math.abs(arg.value);

    return {
        type: NUMBER,
        value: res
    };
}

function maxInt(_ctx: EvalContext, ...args: IntegerValue[]): EvalValue {
    if (!args.length) {
        throw new Error('Function requires non empty argument list.');
    }

    let max = args[0].value;
    for (let i = 1; i < args.length; ++i) {
        if (args[i].value > max) {
            max = args[i].value;
        }
    }

    return {
        type: INTEGER,
        value: max
    };
}

function maxNum(_ctx: EvalContext, ...args: NumberValue[]): EvalValue {
    if (!args.length) {
        throw new Error('Function requires non empty argument list.');
    }

    return {
        type: NUMBER,
        value: Math.max(...args.map(arg => arg.value))
    };
}

function minInt(_ctx: EvalContext, ...args: IntegerValue[]): EvalValue {
    if (!args.length) {
        throw new Error('Function requires non empty argument list.');
    }

    let min = args[0].value;
    for (let i = 1; i < args.length; ++i) {
        if (args[i].value < min) {
            min = args[i].value;
        }
    }

    return {
        type: INTEGER,
        value: min
    };
}

function minNum(_ctx: EvalContext, ...args: NumberValue[]): EvalValue {
    if (!args.length) {
        throw new Error('Function requires non empty argument list.');
    }

    return {
        type: NUMBER,
        value: Math.min(...args.map(arg => arg.value))
    };
}

function maxNumber(): EvalValue {
    return {
        type: NUMBER,
        value: MAX_NUMBER
    };
}

function minNumber(): EvalValue {
    return {
        type: NUMBER,
        value: MIN_NUMBER
    };
}

function maxInteger(ctx: EvalContext): EvalValue {
    checkIntegerOverflow(ctx, MAX_INT);

    return {
        type: INTEGER,
        value: MAX_INT
    };
}

function minInteger(ctx: EvalContext): EvalValue {
    checkIntegerOverflow(ctx, MIN_INT);

    return {
        type: INTEGER,
        value: MIN_INT
    };
}

function round(_ctx: EvalContext, arg: NumberValue): EvalValue {
    const sign = Math.sign(arg.value);

    return {
        type: NUMBER,
        // js treats Math.round(-0.5) as 0, which is different to other platforms
        value: sign * Math.round(Math.abs(arg.value))
    };
}

function floor(_ctx: EvalContext, arg: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.floor(arg.value)
    };
}

function ceil(_ctx: EvalContext, arg: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.ceil(arg.value)
    };
}

function signumInteger(_ctx: EvalContext, arg: IntegerValue): EvalValue {
    return {
        type: INTEGER,
        value: signBigInt(arg.value)
    };
}

function signumNumber(_ctx: EvalContext, arg: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.sign(arg.value)
    };
}

function copySignInteger(ctx: EvalContext, arg0: IntegerValue, arg1: IntegerValue): EvalValue {
    let res: bigint;

    if (arg1.value === bigIntZero) {
        res = arg0.value;
    } else if (arg0.value === bigIntZero) {
        res = toBigInt(0);
    } else {
        const sign = signBigInt(arg1.value);

        res = absBigInt(arg0.value) * sign;
    }

    checkIntegerOverflow(ctx, res);

    return {
        type: INTEGER,
        value: res
    };
}

function copySignNumber(_ctx: EvalContext, arg0: NumberValue, arg1: NumberValue): EvalValue {
    let sign = Math.sign(arg1.value);

    if (sign === 0) {
        sign = Object.is(sign, 0) ? 1 : -1;
    }

    const res = Math.abs(arg0.value) * sign;

    return {
        type: NUMBER,
        value: res
    };
}

export function registerMath(): void {
    registerFunc('div', [INTEGER, INTEGER], divInteger);
    registerFunc('div', [NUMBER, NUMBER], divNumber);

    registerFunc('mod', [INTEGER, INTEGER], modInteger);
    registerFunc('mod', [NUMBER, NUMBER], modNumber);

    registerFunc('mul', [{
        type: INTEGER,
        isVararg: true
    }], mulInteger);
    registerFunc('mul', [{
        type: NUMBER,
        isVararg: true
    }], mulNumber);

    registerFunc('sub', [{
        type: INTEGER,
        isVararg: true
    }], subInteger);
    registerFunc('sub', [{
        type: NUMBER,
        isVararg: true
    }], subNumber);

    registerFunc('sum', [{
        type: INTEGER,
        isVararg: true
    }], sumInteger);
    registerFunc('sum', [{
        type: NUMBER,
        isVararg: true
    }], sumNumber);

    registerFunc('abs', [INTEGER], absInteger);
    registerFunc('abs', [NUMBER], absNumber);

    registerFunc('max', [{
        type: INTEGER,
        isVararg: true
    }], maxInt);
    registerFunc('max', [{
        type: NUMBER,
        isVararg: true
    }], maxNum);

    registerFunc('min', [{
        type: INTEGER,
        isVararg: true
    }], minInt);
    registerFunc('min', [{
        type: NUMBER,
        isVararg: true
    }], minNum);

    registerFunc('maxNumber', [], maxNumber);
    registerFunc('minNumber', [], minNumber);

    registerFunc('maxInteger', [], maxInteger);
    registerFunc('minInteger', [], minInteger);

    registerFunc('round', [NUMBER], round);
    registerFunc('floor', [NUMBER], floor);
    registerFunc('ceil', [NUMBER], ceil);

    registerFunc('signum', [INTEGER], signumInteger);
    registerFunc('signum', [NUMBER], signumNumber);

    registerFunc('copySign', [INTEGER, INTEGER], copySignInteger);
    registerFunc('copySign', [NUMBER, NUMBER], copySignNumber);
}
