export function getTankerKeyRaw(value: string): string | undefined {
    // variable value = `#{tanker/${key}}`;
    // value = `@{key}`;
    const match = value.match(/^\@\{(.+)\}$/);
    return match?.[1];
}

export function tankerKeyToVariableName(key: string): string {
    return 'tanker_' + key.replace(/[^a-z_]/g, '_');
}
