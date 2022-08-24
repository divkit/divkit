import { DivExpression, UnsafeDivExpression } from './unsafe-expression';

export function expression(expression: string): DivExpression {
    return new UnsafeDivExpression(expression);
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

export { DivExpression };
