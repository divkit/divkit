/**
 * Escapes html
 * @param str
 * @returns
 */
export function htmlFilter(str: string): string {
    let str2 = String(str);

    if (str2.indexOf('&') > -1) {
        str2 = str2.replace(/&/g, '&amp;');
    }
    if (str2.indexOf('<') > -1) {
        str2 = str2.replace(/</g, '&lt;');
    }
    if (str2.indexOf('>') > -1) {
        str2 = str2.replace(/>/g, '&gt;');
    }
    if (str2.indexOf('"') > -1) {
        str2 = str2.replace(/"/g, '&quot;');
    }

    return str2;
}
