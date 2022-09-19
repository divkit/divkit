export function correctBooleanInt(val: number | boolean | undefined, defaultVal: boolean): boolean {
    if (val === 1 || val === 0 || val === false || val === true) {
        return Boolean(val);
    }
    return defaultVal;
}
