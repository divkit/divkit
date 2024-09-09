export function formatSize(val: number | undefined): string {
    if (val === undefined) {
        return '';
    }

    return val
        .toFixed(2)
        .replace(/(\.\d+?)0+$/, '$1')
        .replace(/\.0$/, '');
}
