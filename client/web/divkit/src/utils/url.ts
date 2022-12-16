export function getUrlSchema(url: string): string {
    const match = /^([^/]+):\/\//.exec(url);

    return match && match[1] || '';
}

export function isBuiltinSchema(schema: string, builtinSchemas: Set<string>): boolean {
    return builtinSchemas.has(schema);
}
