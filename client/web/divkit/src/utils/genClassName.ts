import type { Mods } from '../types/general';

function push(list: string[], val: string | undefined): void {
    if (val) {
        list.push(val);
    }
}

export function genClassName(
    component: string,
    css: Record<string, string>,
    mods: Mods
): string {
    const res: string[] = [];

    push(res, css[component]);

    for (const key in mods) {
        if (mods.hasOwnProperty(key)) {
            const val = mods[key];
            if (val) {
                const cssKey = `${component}_${key}` + (typeof val === 'string' ? `_${val}` : '');

                push(res, css[cssKey]);
            }
        }
    }

    return res.join(' ');
}
