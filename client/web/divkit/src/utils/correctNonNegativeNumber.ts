export function correctNonNegativeNumber(val: number | undefined, defaultVal: number): number {
    const num = Number(val);
    if (isNaN(num) || num < 0) {
        return defaultVal;
    }
    return num;
}
