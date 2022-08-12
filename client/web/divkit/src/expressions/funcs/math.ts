import type { EvalValue, IntegerValue, NumberValue } from '../eval';
import { registerFunc } from './funcs';
import { INTEGER, MAX_INT, MAX_NUMBER, MIN_INT, MIN_NUMBER, NUMBER } from '../const';
import { checkIntegerOverflow, roundInteger } from '../utils';

function div<T extends IntegerValue | NumberValue>(arg0: T, arg1: T): EvalValue {
    if (arg1.value === 0) {
        throw new Error('Division by zero is not supported.');
    }

    let res = arg0.value / arg1.value;
    if (arg0.type === INTEGER) {
        res = roundInteger(res);
    }

    return {
        type: arg0.type,
        value: res
    };
}

function mod<T extends IntegerValue | NumberValue>(arg0: T, arg1: T): EvalValue {
    if (arg1.value === 0) {
        throw new Error('Division by zero is not supported.');
    }

    let res = arg0.value % arg1.value;
    if (arg0.type === INTEGER) {
        res = roundInteger(res);
    }

    return {
        type: arg0.type,
        value: res
    };
}

function mul<T extends IntegerValue | NumberValue>(...args: T[]): EvalValue {
    let res = args.length ? args[0].value : 0;
    for (let i = 1; i < args.length; ++i) {
        res *= args[i].value;
        if (args[0].type === INTEGER) {
            res = roundInteger(res);
        }
    }

    return {
        type: args[0].type,
        value: res
    };
}

function sub<T extends IntegerValue | NumberValue>(...args: T[]): EvalValue {
    let res = args.length ? args[0].value : 0;
    for (let i = 1; i < args.length; ++i) {
        res -= args[i].value;
        if (args[0].type === INTEGER) {
            res = roundInteger(res);
        }
    }

    return {
        type: args.length ? args[0].type : INTEGER,
        value: res
    };
}

function sum<T extends IntegerValue | NumberValue>(...args: T[]): EvalValue {
    let res = 0;
    for (let i = 0; i < args.length; ++i) {
        res += args[i].value;
        if (args[0].type === INTEGER) {
            res = roundInteger(res);
        }
    }

    return {
        type: args.length ? args[0].type : INTEGER,
        value: res
    };
}

function abs(arg: IntegerValue | NumberValue): EvalValue {
    const res = Math.abs(arg.value);

    if (arg.type === INTEGER) {
        checkIntegerOverflow(res);
    }

    return {
        type: arg.type,
        value: res
    };
}

function max<T extends IntegerValue | NumberValue>(...args: T[]): EvalValue {
    if (!args.length) {
        throw new Error('Non empty argument list is required.');
    }

    return {
        type: args[0].type,
        value: Math.max(...args.map(arg => arg.value))
    };
}

function min<T extends IntegerValue | NumberValue>(...args: T[]): EvalValue {
    if (!args.length) {
        throw new Error('Non empty argument list is required.');
    }

    return {
        type: args[0].type,
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

function maxInteger(): EvalValue {
    return {
        type: INTEGER,
        value: MAX_INT
    };
}

function minInteger(): EvalValue {
    return {
        type: INTEGER,
        value: MIN_INT
    };
}

function round(arg: NumberValue): EvalValue {
    const sign = Math.sign(arg.value);


    return {
        type: NUMBER,
        // js treats Math.round(-0.5) as 0, which is different to other platforms
        value: sign * Math.round(Math.abs(arg.value))
    };
}

function floor(arg: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.floor(arg.value)
    };
}

function ceil(arg: NumberValue): EvalValue {
    return {
        type: NUMBER,
        value: Math.ceil(arg.value)
    };
}

function signum(arg: IntegerValue | NumberValue): EvalValue {
    return {
        type: arg.type,
        value: Math.sign(arg.value)
    };
}

function copySign<T extends IntegerValue | NumberValue>(arg0: T, arg1: T): EvalValue {
    let res: number;

    if (arg1.value === 0 && arg0.type === INTEGER) {
        res = arg0.value;
    } else if (arg0.value === 0 && arg0.type === INTEGER) {
        res = 0;
    } else {
        let sign = Math.sign(arg1.value);

        if (arg0.type === NUMBER && sign === 0) {
            sign = Object.is(sign, 0) ? 1 : -1;
        }

        res = Math.abs(arg0.value) * sign;
    }

    if (arg0.type === INTEGER) {
        checkIntegerOverflow(res);
    }

    return {
        type: arg0.type,
        value: res
    };
}

export function registerMath(): void {
    registerFunc('div', [INTEGER, INTEGER], div);
    registerFunc('div', [NUMBER, NUMBER], div);

    registerFunc('mod', [INTEGER, INTEGER], mod);
    registerFunc('mod', [NUMBER, NUMBER], mod);

    registerFunc('mul', [{
        type: INTEGER,
        isVararg: true
    }], mul);
    registerFunc('mul', [{
        type: NUMBER,
        isVararg: true
    }], mul);

    registerFunc('sub', [{
        type: INTEGER,
        isVararg: true
    }], sub);
    registerFunc('sub', [{
        type: NUMBER,
        isVararg: true
    }], sub);

    registerFunc('sum', [{
        type: INTEGER,
        isVararg: true
    }], sum);
    registerFunc('sum', [{
        type: NUMBER,
        isVararg: true
    }], sum);

    registerFunc('abs', [INTEGER], abs);
    registerFunc('abs', [NUMBER], abs);

    registerFunc('max', [{
        type: INTEGER,
        isVararg: true
    }], max);
    registerFunc('max', [{
        type: NUMBER,
        isVararg: true
    }], max);

    registerFunc('min', [{
        type: INTEGER,
        isVararg: true
    }], min);
    registerFunc('min', [{
        type: NUMBER,
        isVararg: true
    }], min);

    registerFunc('maxNumber', [], maxNumber);
    registerFunc('minNumber', [], minNumber);

    registerFunc('maxInteger', [], maxInteger);
    registerFunc('minInteger', [], minInteger);

    registerFunc('round', [NUMBER], round);
    registerFunc('floor', [NUMBER], floor);
    registerFunc('ceil', [NUMBER], ceil);

    registerFunc('signum', [NUMBER], signum);
    registerFunc('signum', [INTEGER], signum);

    registerFunc('copySign', [NUMBER, NUMBER], copySign);
    registerFunc('copySign', [INTEGER, INTEGER], copySign);
}
