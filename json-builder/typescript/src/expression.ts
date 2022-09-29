import { DivExpression, SafeDivExpression } from './safe-expression';

export function expression(expression: string): DivExpression {
    return new SafeDivExpression(expression);
}

const replacer: Record<string, string> = {
    '\\': '\\\\',
    '@{': '\\@{',
};

export function escapeExpression(str: string): string {
    return str.replace(/\\|(@{)/g, (full) => {
        return replacer[full];
    });
}
