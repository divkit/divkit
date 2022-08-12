export function isNumber(val: number | undefined): val is number {
    if (typeof val !== 'number' && typeof val !== 'string') {
        return false;
    }

    const num = Number(val);

    return !isNaN(num);
}
