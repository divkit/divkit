import type { Node } from './ast';
import type { VariableValue } from './variable';
import { uniq } from '../utils/uniq';
import { parse } from './expressions';
import { evalExpression, VariablesMap } from './eval';
import { containsUnsetVariables, dateToString, gatherVarsFromAst } from './utils';
import { LogError, wrapError } from '../utils/wrapError';

const EXPR_REGEXP = /(?:[^\\]|^)@{.+}/i;

class ExpressionBinding {
    private readonly ast: Node;

    constructor(ast: Node) {
        this.ast = ast;
    }

    /**
     * Applies variables into ast
     * @param variables
     * @param logError
     */
    apply(variables: VariablesMap, logError: LogError): VariableValue | string | undefined {
        if (containsUnsetVariables(this.ast, variables)) {
            return undefined;
        }

        try {
            const result = evalExpression(variables, this.ast);
            if (result.type === 'error') {
                logError(wrapError(new Error('Expression execution error'), {
                    additional: {
                        message: result.value
                    }
                }));
                return undefined;
            }

            const value = result.value;
            if (value instanceof Date) {
                return dateToString(value);
            }
            return value;
        } catch (err) {
            logError(wrapError(new Error('Expression execution error')));
            return undefined;
        }
    }
}

export type MaybeMissing<T> = T | (
    T extends (infer U)[] ?
        MaybeMissing<U>[] :
        (
            T extends object ?
                {
                    [P in keyof T]: MaybeMissing<T[P]>;
                } :
                T | undefined
        )
);

function hasExpressions(str: string): boolean {
    return EXPR_REGEXP.test(str);
}

function prepareVarsObj<T>(jsonProp: T, vars: string[], logError: LogError): unknown {
    if (jsonProp) {
        if (typeof jsonProp === 'string') {
            if (hasExpressions(jsonProp)) {
                try {
                    const ast = parse(jsonProp, {
                        startRule: 'JsonStringContents'
                    });
                    const propVars = gatherVarsFromAst(ast);
                    vars.push(...propVars);

                    return new ExpressionBinding(ast);
                } catch (err) {
                    logError(wrapError(new Error('Unable to parse expression'), {
                        additional: {
                            expression: jsonProp
                        }
                    }));
                    return undefined;
                }
            }
        } else if (Array.isArray(jsonProp)) {
            return jsonProp.map(item => prepareVarsObj(item, vars, logError));
        } else if (typeof jsonProp === 'object') {
            const res: Record<string, unknown> = {};
            for (const key in jsonProp) {
                res[key] = prepareVarsObj(jsonProp[key], vars, logError);
            }
            return res;
        }
    }
    return jsonProp;
}

function applyVars(jsonProp: unknown, variables: VariablesMap, logError: LogError): unknown {
    if (jsonProp) {
        if (jsonProp instanceof ExpressionBinding) {
            return jsonProp.apply(variables, logError);
        } else if (Array.isArray(jsonProp)) {
            return jsonProp.map(it => applyVars(it, variables, logError));
        } else if (typeof jsonProp === 'object') {
            const res: Record<string, unknown> = {};
            for (const key in jsonProp) {
                res[key] = applyVars(jsonProp[key as keyof typeof jsonProp], variables, logError);
            }
            return res;
        }
    }
    return jsonProp;
}

export function prepareVars<T>(jsonProp: T, logError: LogError): {
    vars: string[];
    applyVars: (variables: VariablesMap) => MaybeMissing<T>;
} {
    let vars: string[] = [];
    const root = prepareVarsObj(jsonProp, vars, logError);

    vars = uniq(vars);

    return {
        vars,
        applyVars(variables: VariablesMap) {
            return applyVars(root, variables, logError) as MaybeMissing<T>;
        }
    };
}
