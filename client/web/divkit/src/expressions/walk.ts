import type { Node } from './ast';

export function walk(ast: Node, visitors: {
    [Type in Node['type']]?: (node: Extract<Node, { type: Type }>) => void;
}): void {
    visitors[ast.type]?.(ast as any);

    switch (ast.type) {
        case 'TemplateLiteral':
            ast.expressions.forEach(item => {
                walk(item, visitors);
            });
            break;
        case 'BinaryExpression':
        case 'LogicalExpression':
            walk(ast.left, visitors);
            walk(ast.right, visitors);
            break;
        case 'UnaryExpression':
            walk(ast.argument, visitors);
            break;
        case 'ConditionalExpression':
            walk(ast.test, visitors);
            walk(ast.consequent, visitors);
            walk(ast.alternate, visitors);
            break;
        case 'TryExpression':
            walk(ast.test, visitors);
            walk(ast.alternate, visitors);
            break;
        case 'CallExpression':
            ast.arguments.forEach(item => {
                walk(item, visitors);
            });
            break;
    }
}
