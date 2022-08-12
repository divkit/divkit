export function padLeft(val: string, count: number): string {
    while (val.length < count) {
        val = '0' + val;
    }
    return val;
}
