export function correctBooleanInt(val: number | undefined, defaultVal: boolean): boolean {
    if (val === 1 || val === 0) {
        return Boolean(val);
    }
    return defaultVal;
}
