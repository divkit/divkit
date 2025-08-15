export function encodeDivString(str: string): string {
    return str
        .replace(/\\/g, '\\\\')
        .replace(/'/g, '\\');
}
