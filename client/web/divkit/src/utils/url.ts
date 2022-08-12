export function getUrlSchema(url: string): string {
    const match = /^([^/]+):\/\//.exec(url);

    return match && match[1] || '';
}

const BUILTIN_SCHEMAS = new Set(['http', 'https', 'tel', 'mailto', 'intent']);

export function isBuiltinSchema(schema: string): boolean {
    return BUILTIN_SCHEMAS.has(schema);
}
