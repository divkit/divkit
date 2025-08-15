export function formatSize(val: number | undefined, digits = 2): string {
    if (val === undefined) {
        return '';
    }

    return val
        .toFixed(digits)
        .replace(/(\.\d+?)0+$/, '$1')
        .replace(/\.0$/, '');
}
