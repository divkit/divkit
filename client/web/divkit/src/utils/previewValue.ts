export function previewValue(value: unknown): string {
    if (typeof value === 'object' && value) {
        if (Array.isArray(value)) {
            return 'array';
        }
        return 'object';
    } else if (value === null) {
        return 'null';
    } else if (value === undefined) {
        return 'undefined';
    }
    return JSON.stringify(value);
}
