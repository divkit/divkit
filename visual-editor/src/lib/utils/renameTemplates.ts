/* eslint-disable @typescript-eslint/no-explicit-any */

function renameTemplate(card: any, renameMap: Map<string, string>): any {
    const proc = (obj: unknown): void => {
        if (!(typeof obj === 'object' && obj)) {
            return;
        }

        if ('type' in obj && typeof obj.type === 'string' && renameMap.has(obj.type)) {
            obj.type = renameMap.get(obj.type);
        }

        for (const key in obj) {
            proc(obj[key as keyof typeof obj]);
        }
    };

    proc(card);

    return card;
}

export function addTemplatesSuffix(card: any, suffix: string): any {
    if (!card?.templates) {
        return card;
    }

    const renameMap = new Map<string, string>();
    const newTemplates: Record<string, unknown> = {};
    for (const key in card.templates) {
        renameMap.set(key, key + suffix);
        newTemplates[key + suffix] = card.templates[key];
    }
    card.templates = newTemplates;

    return renameTemplate(card, renameMap);
}

export function removeTemplatesSuffix(card: any, clearSuffix: (str: string) => string | undefined): any {
    if (!card?.templates) {
        return card;
    }

    const renameMap = new Map<string, string>();
    const newTemplates: Record<string, unknown> = {};
    for (const key in card.templates) {
        const cleared = clearSuffix(key);
        if (cleared) {
            renameMap.set(key, cleared);
            newTemplates[cleared] = card.templates[key];
        } else {
            newTemplates[key] = card.templates[key];
        }
    }
    card.templates = newTemplates;

    return renameTemplate(card, renameMap);
}
