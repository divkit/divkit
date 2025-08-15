import type { VariableValue } from '../expressions/variable';

export function propToString(val: VariableValue | undefined): string {
    const valToString = String(val ?? '');

    return valToString;
}
