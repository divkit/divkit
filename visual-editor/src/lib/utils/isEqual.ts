export function isEqual(obj0: unknown, obj1: undefined | object): boolean {
    if (typeof obj0 !== typeof obj1) {
        return false;
    }

    if (typeof obj0 !== 'object' || !obj0) {
        return obj0 === obj1;
    }

    if (!obj1) {
        return obj0 === obj1;
    }

    for (const key in obj0) {
        if (obj0.hasOwnProperty(key) && !obj1.hasOwnProperty(key)) {
            return false;
        }
        if (!isEqual(obj0[key as keyof typeof obj0], obj1[key as keyof typeof obj1])) {
            return false;
        }
    }
    for (const key in obj1) {
        if (obj1.hasOwnProperty(key) && !obj0.hasOwnProperty(key)) {
            return false;
        }
    }

    return true;
}
