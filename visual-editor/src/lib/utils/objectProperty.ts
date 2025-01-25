import { copyValue } from './copyValue';

export function getObjectProperty(obj: object, name: string): unknown {
    const parts = name.split(/\.|\[([^\]]+)]/g);
    let curObj = obj;

    let i = 0;
    while (i < parts.length) {
        const key = parts[i] as keyof typeof curObj;
        if (i + 1 === parts.length) {
            return curObj[key];
        }
        if (!curObj[key]) {
            return;
        }
        curObj = curObj[key];
        ++i;
        if (parts[i] !== undefined) {
            const key = parts[i] as keyof typeof curObj;

            if (i + 2 === parts.length) {
                return curObj[key];
            }
            if (!curObj[key]) {
                return;
            }
            curObj = curObj[key];
            i += 3;
        } else {
            ++i;
        }
    }
}

export function setObjectProperty(obj: object, name: string, value: unknown): void {
    const parts = name.split(/\.|\[([^\]]+)]/g);
    const trail: {
        obj: Record<string, unknown>;
        prop: string;
    }[] = [];
    let curObj = obj as Record<string, unknown>;

    const clean = () => {
        for (let i = trail.length - 1; i >= 0; --i) {
            const { obj, prop } = trail[i];
            const val = obj[prop];
            if (Array.isArray(val) && !val.length) {
                delete obj[prop];
            } else if (typeof val === 'object' && val !== null && !Object.keys(val).length) {
                delete obj[prop];
            } else {
                break;
            }
        }
    };

    let i = 0;
    while (i < parts.length) {
        const key = parts[i] as keyof typeof curObj;
        if (i + 1 === parts.length) {
            if (!value && value !== 0) {
                if (Array.isArray(curObj)) {
                    curObj.splice(Number(parts[i]), 1);
                } else {
                    delete curObj[key];
                }
                clean();
            } else {
                curObj[key] = value;
            }
            if (i === 0 && curObj[key] !== undefined) {
                curObj[key] = copyValue(curObj[key]);
            }
            ++i;
        } else {
            curObj[key] = curObj[key] || (parts[i + 1] !== undefined ? [] : {});
            if (i === 0 && curObj[key] !== undefined) {
                curObj[key] = copyValue(curObj[key]);
            }
            trail.push({
                obj: curObj,
                prop: parts[i]
            });
            curObj = curObj[key] as Record<string, unknown>;
            ++i;
            if (parts[i] !== undefined) {
                const key = parts[i] as keyof typeof curObj;

                if (i + 2 === parts.length) {
                    // eslint-disable-next-line max-depth
                    if (value !== undefined) {
                        curObj[key] = value;
                    } else {
                        // eslint-disable-next-line max-depth
                        if (Array.isArray(curObj)) {
                            curObj.splice(Number(parts[i]), 1);
                        } else {
                            delete curObj[key];
                        }
                        clean();
                    }
                    break;
                } else {
                    curObj[key] = curObj[key] || {};
                    trail.push({
                        obj: curObj,
                        prop: parts[i]
                    });
                    curObj = curObj[key] as Record<string, unknown>;
                    i += 3;
                }
            } else {
                ++i;
            }
        }
    }
}
