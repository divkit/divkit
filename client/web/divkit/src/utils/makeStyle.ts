/**
 * Собирает строчку стилей из объекта
 * @param styles
 * @returns
 */
export function makeStyle(styles?: Record<string, string | number | undefined>): string | undefined {
    if (!styles) {
        return undefined;
    }

    let res = '';
    for (const key in styles) {
        if (styles.hasOwnProperty(key)) {
            if (!styles[key] && styles[key] !== 0) {
                continue;
            }
            if (res) {
                res += ';';
            }
            res += key + ':' + String(styles[key]);
        }
    }
    return res || undefined;
}
