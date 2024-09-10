const replacer: Record<string, string> = {
    '\\': '\\\\',
    '@{': '\\@{',
};

export function escapeExpression(str: string): string {
    return str.replace(/\\|(@{)/g, full => {
        return replacer[full];
    });
}
