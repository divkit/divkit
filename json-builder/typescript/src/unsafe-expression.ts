const expr = Symbol();

export interface DivExpression {
    [expr]: string;
}

export class UnsafeDivExpression implements DivExpression {
    public [expr]: string;

    public constructor(expression: string) {
        this[expr] = expression;
    }

    public toJSON(): string {
        return this[expr];
    }

    public toString(): string {
        return this[expr];
    }
}
