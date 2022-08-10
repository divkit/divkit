export function correctNumber(val: number | undefined, defaultVal: number): number {
    const num = Number(val);
    if (isNaN(num)) {
        return defaultVal;
    }
    return num;
}
