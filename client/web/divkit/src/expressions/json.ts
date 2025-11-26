/* eslint-disable max-depth */
import type { Node } from './ast';
import type { Variable, VariableValue } from './variable';
import type { Store } from '../../typings/store';
import { uniq } from '../utils/uniq';
import { parse } from './expressions';
import { evalExpression, type VariablesMap } from './eval';
import { dateToString, gatherVarsFromAst, stringifyColor } from './utils';
import { type LogError, wrapError } from '../utils/wrapError';
import { parseColor } from '../utils/correctColor';
import { MAX_INT32, MIN_INT32 } from './const';
import { simpleUnescapeString } from './simpleUnescapeString';
import { cacheGet, cacheSet } from './parserCache';
import type { CustomFunctions } from './funcs/customFuncs';

class ExpressionBinding {
    private readonly ast: Node;
    private readonly expr: string;

    constructor(ast: Node, expr: string) {
        this.ast = ast;
        this.expr = expr;
    }

    /**
     * Applies variables into ast
     * @param variables
     * @param logError
     */
    apply<T>(
        {
            variables,
            customFunctions,
            logError,
            store,
            weekStartDay,
            keepComplex
        }: {
            variables: VariablesMap;
            customFunctions: CustomFunctions | undefined;
            logError: LogError;
            store: Store | undefined;
            weekStartDay: number;
            keepComplex?: boolean;
        }
    ): {
        result: T;
        usedVars?: Set<Variable>;
    } {
        let res: ReturnType<typeof evalExpression> | undefined;

        try {
            res = evalExpression(variables, customFunctions, store, this.ast, {
                weekStartDay
            });
            res.warnings.forEach(logError);
            const result = res.result;

            if (result.type === 'error') {
                logError(wrapError(new Error('Expression execution error'), {
                    additional: {
                        message: result.value,
                        expression: this.expr
                    }
                }));
                return {
                    result: undefined as T,
                    usedVars: res.usedVars
                };
            }

            const value = result.value;
            if (value instanceof Date) {
                return {
                    result: dateToString(value) as T,
                    usedVars: res.usedVars
                };
            }
            if (result.type === 'boolean') {
                return {
                    result: Boolean(value) as T,
                    usedVars: res.usedVars
                };
            }
            if (result.type === 'color') {
                const parsed = parseColor(String(value));
                if (parsed) {
                    return {
                        result: stringifyColor(parsed) as T,
                        usedVars: res.usedVars
                    };
                }
                logError(wrapError(new Error('Expression execution error')));
            }
            if (result.type === 'integer') {
                if ((value as number) > MAX_INT32 || (value as number) < MIN_INT32) {
                    logError(wrapError(new Error('Expression result is out of 32-bit int range')));
                    return {
                        result: undefined as T,
                        usedVars: res.usedVars
                    };
                }
                return {
                    result: Number(value) as T,
                    usedVars: res.usedVars
                };
            }
            if (result.type === 'function') {
                return {
                    result: `<${result.value[0]?.name || 'Function'}>` as T,
                    usedVars: res.usedVars
                };
            }
            if (!keepComplex && (result.type === 'array' || result.type === 'dict')) {
                try {
                    return {
                        result: JSON.stringify(value) as T,
                        usedVars: res.usedVars
                    };
                } catch (err) {
                    logError(wrapError(new Error(`Failed to stringify ${result.type}`)));
                    return {
                        result: `<${result.type}>` as T,
                        usedVars: res.usedVars
                    };
                }
            }
            return {
                result: value as T,
                usedVars: res.usedVars
            };
        } catch (err) {
            logError(wrapError(new Error('Expression execution error'), {
                additional: {
                    expression: this.expr
                }
            }));
            return {
                result: undefined as T,
                usedVars: res?.usedVars
            };
        }
    }
}

class VariableBinding {
    private readonly variable: string;

    constructor(variable: string) {
        this.variable = variable;
    }

    /**
     * Applies variables into ast
     * @param variables
     * @param logError
     */
    apply(variables: VariablesMap): VariableValue | string | undefined {
        const varInstance = variables.get(this.variable);
        if (varInstance) {
            return varInstance.getValue();
        }

        return undefined;
    }
}

export type MaybeMissing<T> = T | (
    T extends (infer U)[] ?
        MaybeMissing<U>[] :
        (
            T extends object ?
                {
                    [P in keyof T]?: MaybeMissing<T[P]>;
                } :
                T | undefined
        )
);

function hasExpressions(str: string): boolean {
    return str.indexOf('@{') > -1 || str.indexOf('\\') > -1;
}

function prepareVarsObj<T>(
    jsonProp: T,
    store: {
        vars: string[];
        hasExpression: boolean;
    },
    logError: LogError
): unknown {
    if (jsonProp) {
        if (typeof jsonProp === 'string') {
            if (hasExpressions(jsonProp)) {
                store.hasExpression = true;

                if (process.env.ENABLE_EXPRESSIONS || process.env.ENABLE_EXPRESSIONS === undefined) {
                    try {
                        const ast = cacheGet(jsonProp) || parse(jsonProp, {
                            startRule: 'JsonStringContents'
                        });
                        cacheSet(jsonProp, ast);
                        const propVars = gatherVarsFromAst(ast);
                        store.vars.push(...propVars);

                        return new ExpressionBinding(ast, jsonProp);
                    } catch (err) {
                        logError(wrapError(new Error('Unable to parse expression'), {
                            additional: {
                                expression: jsonProp
                            }
                        }));
                        return undefined;
                    }
                } else {
                    if (jsonProp === '@{}') {
                        return '';
                    } else if (jsonProp.startsWith('@{') && jsonProp.endsWith('}')) {
                        return new VariableBinding(jsonProp.substring(2, jsonProp.length - 1));
                    }
                    try {
                        return simpleUnescapeString(jsonProp);
                    } catch (err: any) {
                        logError(wrapError(err as Error, {
                            additional: {
                                expression: jsonProp
                            }
                        }));
                        return undefined;
                    }
                }
            }
        } else if (Array.isArray(jsonProp)) {
            return jsonProp.map(item => prepareVarsObj(item, store, logError));
        } else if (typeof jsonProp === 'object') {
            const res: Record<string, unknown> = {};
            for (const key in jsonProp) {
                res[key] = prepareVarsObj(jsonProp[key], store, logError);
            }
            return res;
        }
    }
    return jsonProp;
}

function applyVars<T>(
    jsonProp: T,
    opts: {
        variables: VariablesMap;
        customFunctions: CustomFunctions | undefined;
        logError: LogError;
        store: Store | undefined;
        weekStartDay: number;
        keepComplex?: boolean;
    }
): {
    result: MaybeMissing<T>;
    usedVars?: Set<Variable>;
} {
    if (jsonProp) {
        if (
            (process.env.ENABLE_EXPRESSIONS || process.env.ENABLE_EXPRESSIONS === undefined) &&
            jsonProp instanceof ExpressionBinding
        ) {
            return jsonProp.apply<T>(opts);
        } else if (
            (!process.env.ENABLE_EXPRESSIONS && process.env.ENABLE_EXPRESSIONS !== undefined) &&
            jsonProp instanceof VariableBinding
        ) {
            return {
                result: jsonProp.apply(opts.variables) as T
            };
        } else if (Array.isArray(jsonProp)) {
            let usedVars: Set<Variable> | undefined;
            const arr = jsonProp.map(it => {
                const subres = applyVars(it, opts);

                if (subres.usedVars) {
                    if (!usedVars) {
                        usedVars = new Set();
                    }
                    for (const instance of subres.usedVars) {
                        usedVars.add(instance);
                    }
                }

                return subres.result;
            });

            return {
                result: arr as MaybeMissing<T>,
                usedVars
            };
        } else if (typeof jsonProp === 'object') {
            const res: Record<string, unknown> = {};
            let usedVars: Set<Variable> | undefined;
            for (const key in jsonProp) {
                const subres = applyVars(jsonProp[key as keyof typeof jsonProp], opts);
                res[key] = subres.result;

                if (subres.usedVars) {
                    if (!usedVars) {
                        usedVars = new Set();
                    }
                    for (const instance of subres.usedVars) {
                        usedVars.add(instance);
                    }
                }
            }
            return {
                result: res as MaybeMissing<T>,
                usedVars
            };
        }
    }
    return {
        result: jsonProp
    };
}

export interface PreparedExpression<T> {
    vars: string[];
    hasExpression: boolean;
    applyVars: (
        variables: VariablesMap,
        customFunctions?: CustomFunctions,
        keepComplex?: boolean
    ) => {
        result: MaybeMissing<T>;
        usedVars?: Set<Variable>;
    };
}

export function prepareVars<T>(
    jsonProp: T,
    logError: LogError,
    store: Store | undefined,
    weekStartDay: number
): PreparedExpression<T> {
    const result: {
        vars: string[];
        hasExpression: boolean;
    } = {
        vars: [],
        hasExpression: false
    };
    const root = prepareVarsObj(jsonProp, result, logError);

    const vars = uniq(result.vars);

    return {
        vars,
        hasExpression: result.hasExpression,
        applyVars(variables, customFunctions, keepComplex) {
            return applyVars<T>(root as T, {
                variables,
                customFunctions,
                logError,
                store,
                weekStartDay,
                keepComplex
            });
        }
    };
}
