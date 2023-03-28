import type { EvalTypes, EvalValue } from '../eval';
import type { EvalContext } from '../eval';

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

function matchFuncArgs(func: Func, args: EvalValue[]): {
    type: 'match';
} | FuncMatchError {
    let minArgs = func.args.length;
    let maxArgs = func.args.length;
    const lastArg = func.args[func.args.length - 1];

    if (typeof lastArg === 'object' && lastArg.isVararg) {
        --minArgs;
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

        if (funcArg.type !== args[i].type) {
            return {
                type: 'mismatch',
                expected: funcArg.type,
                found: args[i].type
            };
        }
    }

    return {
        type: 'match'
    };
}

export function findBestMatchedFunc(funcName: string, args: EvalValue[]): Func | FuncMatchError {
    const list = funcs.get(funcName);
    if (!list) {
        return {
            type: 'missing'
        };
    }

    let firstError: FuncMatchError | null = null;
    for (let i = 0; i < list.length; ++i) {
        const match = matchFuncArgs(list[i], args);
        if (match.type === 'match') {
            return list[i];
        }
        if (!firstError) {
            firstError = match;
        }
    }

    if (!firstError) {
        throw new Error('Missing function');
    }

    return firstError;
}
