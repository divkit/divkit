const hasOwnProperty = Object.prototype.hasOwnProperty;

export function isDeepEqual<A, B>(a: A, b: B): boolean {
    if (Object.is(a, b)) {
        return true;
    }

    if (
        typeof a !== 'object' ||
        a === null ||
        typeof b !== 'object' ||
        b === null
    ) {
        return Object.is(a, b);
    }

    const keysA = Object.keys(a);
    const keysB = Object.keys(b);

    if (keysA.length !== keysB.length) {
        return false;
    }

    for (let i = 0; i < keysA.length; i++) {
        const key = keysA[i];

        if (!hasOwnProperty.call(b, key) || !isDeepEqual(a[key as keyof A], b[key as keyof B])) {
            return false;
        }
    }

    return true;
}
