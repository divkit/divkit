/**
 * Конвертирует пиксели приложения в em, чтобы всё скейлилось
 * @param value
 * @returns
 */
export function pxToEm(value: number): string {
    if (typeof value !== 'number' && typeof value !== 'string' || !value) {
        return '0';
    }

    const casted = Number(value);

    if (isNaN(casted)) {
        return '0';
    }

    return (Math.ceil(casted * 1000) / 10000) + 'em';
}

export function pxToEmWithUnits(value: number): string {
    let res = pxToEm(value);

    if (res === '0') {
        res += 'em';
    }

    return res;
}
