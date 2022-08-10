export type Node = BinaryExpression | LogicalExpression |
    UnaryExpression |
    StringLiteral | NumberLiteral | IntegerLiteral | BooleanLiteral |
    TemplateLiteral |
    ConditionalExpression |
    CallExpression |
    Variable;

export type UnaryOperator = '!' | '+' | '-';

export type EqualityOperator = '==' | '!=';

export type CompareOperator = '>' | '>=' | '<' | '<=';

export type SumOperator = '+' | '-';

export type FactorOperator = '/' | '*' | '%';

export type LogicalOperator = '&&' | '||';

export interface BinaryExpression {
    type: 'BinaryExpression',
    operator: EqualityOperator | CompareOperator | SumOperator | FactorOperator;
    left: Node;
    right: Node;
}

export interface LogicalExpression {
    type: 'LogicalExpression',
    operator: LogicalOperator;
    left: Node;
    right: Node;
}

export interface StringLiteral {
    type: 'StringLiteral';
    value: string;
}

export interface TemplateLiteral {
    type: 'TemplateLiteral';
    quasis: StringLiteral[];
    expressions: Node[];
}

export interface NumberLiteral {
    type: 'NumberLiteral';
    value: number;
}

export interface IntegerLiteral {
    type: 'IntegerLiteral';
    value: number;
}

export interface BooleanLiteral {
    type: 'BooleanLiteral';
    value: boolean;
}

export interface ConditionalExpression {
    type: 'ConditionalExpression';
    test: Node;
    consequent: Node;
    alternate: Node;
}

export interface Identifier {
    type: 'Identifier';
    name: string;
}

export interface Variable {
    type: 'Variable';
    id: Identifier;
}

export interface UnaryExpression {
    type: 'UnaryExpression';
    operator: UnaryOperator;
    argument: Node;
}

export interface CallExpression {
    type: 'CallExpression';
    callee: Identifier;
    arguments: Node[];
}
