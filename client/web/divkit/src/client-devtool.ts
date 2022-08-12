import type { Variable } from './expressions/variable';
import { parse } from './expressions/expressions';
import { evalExpression as evalExpressionInner, EvalResult } from './expressions/eval';
import { Node } from './expressions/ast';
import { funcs } from './expressions/funcs/funcs';

export * from './client';

export function evalExpression(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): EvalResult {
    let ast: Node;
    try {
        ast = parse(expr, {
            startRule: opts?.type === 'json' ? 'JsonStringContents' : 'start'
        });
    } catch (err) {
        return {
            type: 'error',
            value: 'Unable to parse expression'
        };
    }
    return evalExpressionInner(opts?.variables || new Map(), ast);
}

export { valToString } from './expressions/utils';

export function functionNames(): string[] {
    return Array.from(funcs.keys());
}
