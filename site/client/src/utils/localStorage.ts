export function getLs(name: string): string | null | undefined {
    try {
        return localStorage.getItem(name);
    } catch (err) {
        return undefined;
    }
}

export function setLs(name: string, value: string): void {
    try {
        return localStorage.setItem(name, value);
    } catch (err) {
        // don't care
    }
}
