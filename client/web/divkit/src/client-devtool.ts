import type { Variable } from './expressions/variable';
import type { Node } from './expressions/ast';
import type { WrappedError } from '../typings/common';
import { parse } from './expressions/expressions';
import { evalExpression as evalExpressionInner, EvalResult } from './expressions/eval';
import { funcs } from './expressions/funcs/funcs';

export * from './client';

export function evalExpression(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): EvalResult {
    return evalExpressionWithFullResult(expr, opts).result;
}

export function evalExpressionWithFullResult(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): {
    result: EvalResult;
    warnings: WrappedError[];
} {
    let ast: Node;
    try {
        ast = parse(expr, {
            startRule: opts?.type === 'json' ? 'JsonStringContents' : 'start'
        });
    } catch (err) {
        return {
            result: {
                type: 'error',
                value: 'Unable to parse expression'
            },
            warnings: []
        };
    }
    return evalExpressionInner(opts?.variables || new Map(), ast);
}

export { valToString } from './expressions/utils';

export function functionNames(): string[] {
    return Array.from(funcs.keys());
}
