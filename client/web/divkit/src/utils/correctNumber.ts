export function correctNumber(val: number | undefined, defaultVal: number): number {
    const num = Number(val);
    if (Number.isNaN(num)) {
        return defaultVal;
    }
    return num;
}
