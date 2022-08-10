import { VariableValue } from '../expressions/variable';

export function propToString(val: VariableValue | undefined, defaultVal: string): string {
    const valToString = String(val ?? '');

    return valToString || defaultVal;
}
