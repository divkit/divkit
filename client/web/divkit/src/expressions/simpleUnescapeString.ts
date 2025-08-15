export function simpleUnescapeString(str: string): string {
    let res = '';
    let isAt = false;
    let isBackslash = false;

    for (let i = 0; i < str.length; ++i) {
        const char = str.charAt(i);
        if (char === '\\') {
            if (isAt) {
                isAt = false;
                res += '@';
            }
            if (isBackslash) {
                res += char;
                isBackslash = false;
            } else {
                isBackslash = true;
            }
        } else if (char === '@') {
            if (isBackslash) {
                res += char;
            } else if (isAt) {
                res += char;
            } else {
                isAt = true;
            }
        } else if (char === '{') {
            if (isAt) {
                throw new Error('Expressions is not supported');
            } else {
                res += char;
            }
        } else {
            if (isBackslash) {
                throw new Error('Incorrect escape');
            }
            if (isAt) {
                isAt = false;
                res += '@';
            }
            res += char;
        }

        if (isBackslash && char !== '\\') {
            isBackslash = false;
        }
    }

    if (isAt) {
        res += '@';
    }
    if (isBackslash) {
        throw new Error('Incorrect escape');
    }

    return res;
}
