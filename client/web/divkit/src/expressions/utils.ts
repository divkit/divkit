import type { EvalValue } from './eval';
import type { Node, Variable } from './ast';
import type { EvalTypes } from './eval';
import type { VariablesMap } from './eval';
import { walk } from './walk';
import { MAX_INT, MIN_INT } from './const';

export function valToInternal(val: EvalValue): EvalValue {
    if (val.type === 'url' || val.type === 'color') {
        return {
            type: 'string',
            value: val.value
        };
    }

    return val;
}

export function dateToString(date: Date): string {
    return date
        .toISOString()
        .replace('T', ' ')
        .replace(/\.\d{3}Z$/, '');
}

export function valToString(val: EvalValue): string {
    val = valToInternal(val);

    if (val.type === 'string') {
        return val.value;
    } else if (val.type === 'integer') {
        return String(val.value);
    } else if (val.type === 'number') {
        let res = String(val.value);

        if (!res.includes('.')) {
            if (res.includes('e')) {
                res = res.replace('e', '.0e');
            } else {
                res += '.0';
            }
        }

        res = res.replace(/e\+?/i, 'E');

        return res;
    } else if (val.type === 'boolean') {
        return val.value ? 'true' : 'false';
    } else if (val.type === 'datetime') {
        return dateToString(val.value);
    }

    throw new Error(`Unexpected type ${val.type}`);
}

export function valToPreview(val: EvalValue): string {
    let res = valToString(val);

    if (val.type === 'string') {
        res = "'" +
            res
                .replace(/\\/g, '\\\'')
                .replace(/'/g, '\\\'') +
            "'";
    }

    return res;
}

export function typeToString(type: EvalTypes): string {
    if (type === 'datetime') {
        return 'DateTime';
    }
    return type.charAt(0).toUpperCase() + type.substring(1);
}

const int32Array = new Int32Array(1);
export function roundInteger(val: number): number {
    int32Array[0] = val;
    return int32Array[0];
}

export function checkIntegerOverflow(val: number): void {
    if (val < MIN_INT || val > MAX_INT) {
        throw new Error('Integer overflow.');
    }
}

export function gatherVarsFromAst(ast: Node): string[] {
    const res: string[] = [];

    walk(ast, {
        Variable(node: Variable): void {
            res.push(node.id.name);
        }
    });

    return res;
}

export function evalError(msg: string, details: string): never {
    throw new Error(`Failed to evaluate [${msg}]. ${details}`);
}

export function containsUnsetVariables(ast: Node, variables: VariablesMap): boolean {
    let result = false;

    walk(ast, {
        Variable(node: Variable): void {
            if (!variables.has(node.id.name)) {
                result = true;
            }
        }
    });

    return result;
}
