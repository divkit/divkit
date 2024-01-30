export function correctPositiveNumber(val: number | undefined, defaultVal: number): number {
    const num = Number(val);
    if (Number.isNaN(num) || num <= 0) {
        return defaultVal;
    }
    return num;
}
