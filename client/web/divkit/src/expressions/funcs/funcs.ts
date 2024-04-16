import { INTEGER, NUMBER } from '../const';
import type { EvalTypes, EvalValue } from '../eval';
import type { EvalContext } from '../eval';
import { integerToNumber } from '../utils';

export type FuncArg = EvalTypes | {
    type: EvalTypes;
    isVararg?: boolean;
}

export interface Func {
    args: FuncArg[];
    cb(ctx: EvalContext, ...args: EvalValue[]): EvalValue;
}

export const funcs: Map<string, Func[]> = new Map();
export const funcByArgs: Map<string, Func> = new Map();
export const methods: Map<string, Func[]> = new Map();
export const methodByArgs: Map<string, Func> = new Map();

type FuncMatchError = {
    type: 'mismatch';
    expected: EvalTypes;
    found: EvalTypes;
} | {
    type: 'few';
    expected: number;
    found: number;
} | {
    type: 'many';
    expected: number;
    found: number;
} | {
    type: 'missing';
};

// no args
export function registerFunc(name: string, args: [], cb: (ctx?: EvalContext) => EvalValue): void;
// one specific arg
export function registerFunc<
    A0 extends EvalTypes
>(
    name: string,
    args: [A0],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>
    ) => EvalValue
): void;
// two specific args
export function registerFunc<
    A0 extends EvalTypes,
    A1 extends EvalTypes
>(
    name: string,
    args: [A0, A1],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>,
        arg1: Extract<EvalValue, { type: A1 }>
    ) => EvalValue
): void;
// three specific args
export function registerFunc<
    A0 extends EvalTypes,
    A1 extends EvalTypes,
    A2 extends EvalTypes
>(
    name: string,
    args: [A0, A1, A2],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>,
        arg1: Extract<EvalValue, { type: A1 }>,
        arg2: Extract<EvalValue, { type: A2 }>
    ) => EvalValue
): void;
// any args
export function registerFunc(
    name: string,
    args: FuncArg[],
    cb: (ctx: EvalContext, ...args: any[]) => EvalValue
): void;

export function registerFunc(
    name: string,
    args: FuncArg[],
    cb: (ctx: EvalContext, ...args: EvalValue[]) => EvalValue
): void {
    const desc: Func = {
        args,
        cb
    };

    const arr = funcs.get(name) || [];

    if (!funcs.has(name)) {
        funcs.set(name, arr);
    }
    arr.push(desc);

    const funcKey = name + ':' + args.map(it => {
        if (typeof it === 'object') {
            return it.type;
        }
        return it;
    }).join('#');

    funcByArgs.set(funcKey, desc);
}

// no args
export function registerMethod(name: string, args: [], cb: (ctx?: EvalContext) => EvalValue): void;
// one specific arg
export function registerMethod<
    A0 extends EvalTypes
>(
    name: string,
    args: [A0],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>
    ) => EvalValue
): void;
// two specific args
export function registerMethod<
    A0 extends EvalTypes,
    A1 extends EvalTypes
>(
    name: string,
    args: [A0, A1],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>,
        arg1: Extract<EvalValue, { type: A1 }>
    ) => EvalValue
): void;
// three specific args
export function registerMethod<
    A0 extends EvalTypes,
    A1 extends EvalTypes,
    A2 extends EvalTypes
>(
    name: string,
    args: [A0, A1, A2],
    cb: (
        ctx: EvalContext,
        arg0: Extract<EvalValue, { type: A0 }>,
        arg1: Extract<EvalValue, { type: A1 }>,
        arg2: Extract<EvalValue, { type: A2 }>
    ) => EvalValue
): void;
// any args
export function registerMethod(
    name: string,
    args: FuncArg[],
    cb: (ctx: EvalContext, ...args: any[]) => EvalValue
): void;

export function registerMethod(
    name: string,
    args: FuncArg[],
    cb: (ctx: EvalContext, ...args: EvalValue[]) => EvalValue
): void {
    const desc: Func = {
        args,
        cb
    };

    const arr = methods.get(name) || [];

    if (!methods.has(name)) {
        methods.set(name, arr);
    }
    arr.push(desc);

    const funcKey = name + ':' + args.map(it => {
        if (typeof it === 'object') {
            return it.type;
        }
        return it;
    }).join('#');

    methodByArgs.set(funcKey, desc);
}

function matchFuncArgs(func: Func, args: EvalValue[]): {
    type: 'match';
    conversions: number;
} | FuncMatchError {
    const minArgs = func.args.length;
    let maxArgs = func.args.length;
    let conversions = 0;
    const lastArg = func.args[func.args.length - 1];

    if (typeof lastArg === 'object' && lastArg.isVararg) {
        maxArgs = Infinity;
    }

    if (args.length < minArgs) {
        return {
            type: 'few',
            expected: minArgs,
            found: args.length
        };
    } else if (args.length > maxArgs) {
        return {
            type: 'many',
            expected: maxArgs,
            found: args.length
        };
    }

    for (let i = 0; i < args.length; ++i) {
        let funcArg = i >= func.args.length ? func.args[func.args.length - 1] : func.args[i];
        if (typeof funcArg !== 'object') {
            funcArg = {
                type: funcArg
            };
        }

        if (funcArg.type === NUMBER && args[i].type === INTEGER) {
            ++conversions;
            continue;
        }

        if (funcArg.type !== args[i].type) {
            return {
                type: 'mismatch',
                expected: funcArg.type,
                found: args[i].type
            };
        }
    }

    return {
        type: 'match',
        conversions
    };
}

export function findBestMatchedFunc(type: 'function' | 'method', funcName: string, args: EvalValue[]): {
    func: Func;
    conversions: number;
} | FuncMatchError {
    const list = (type === 'function' ? funcs : methods).get(funcName);
    if (!list) {
        return {
            type: 'missing'
        };
    }

    let firstError: FuncMatchError | null = null;
    let bestFunc: {
        func: Func;
        conversions: number;
    } | null = null;
    for (let i = 0; i < list.length; ++i) {
        const match = matchFuncArgs(list[i], args);
        if (match.type === 'match') {
            if (!bestFunc || bestFunc.conversions > match.conversions) {
                bestFunc = {
                    func: list[i],
                    conversions: match.conversions
                };
            }
            continue;
        }
        if (!firstError) {
            firstError = match;
        }
    }

    if (!bestFunc) {
        if (firstError) {
            return firstError;
        }
        throw new Error('Missing function');
    }

    return bestFunc;
}

export function convertArgs(func: Func, args: EvalValue[]): EvalValue[] {
    return args.map((arg, i) => {
        let funcArg = i >= func.args.length ? func.args[func.args.length - 1] : func.args[i];
        if (typeof funcArg !== 'object') {
            funcArg = {
                type: funcArg
            };
        }

        if (funcArg.type === NUMBER && arg.type === INTEGER) {
            return integerToNumber(arg);
        }

        return arg;
    });
}
