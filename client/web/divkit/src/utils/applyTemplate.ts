import { wrapError, WrappedError } from './wrapError';
import type { DivBase, TemplateContext } from '../../typings/common';
import type { DivBaseData } from '../types/base';

export function applyTemplate<T extends DivBase>(
    json: T,
    templateContext: TemplateContext,
    templates: Record<string, unknown>,
    logError: (error: WrappedError) => void
): {
    json: T;
    templateContext: TemplateContext;
};
export function applyTemplate(
    json: DivBaseData,
    templateContext: TemplateContext,
    templates: Record<string, unknown>,
    logError: (error: WrappedError) => void
): {
    json: DivBaseData;
    templateContext: TemplateContext;
} {
    const template = templates[json.type];

    if (!template) {
        logError(wrapError(new Error('No such template'), {
            additional: {
                template: json.type
            }
        }));

        return {
            json,
            templateContext
        };
    }

    let i;
    const newContext: TemplateContext = {};

    for (i in templateContext) {
        if (templateContext.hasOwnProperty(i)) {
            newContext[i] = templateContext[i];
        }
    }

    for (i in json) {
        if (i === 'type' || i === '__proto__') {
            continue;
        }

        if (json.hasOwnProperty(i)) {
            newContext[i] = json[i as keyof typeof json];
        }
    }

    function copyTemplated(base: any, extender: any) {
        const keys = Object.keys(extender).filter(key => key !== '__proto__');
        const simpleKeys = keys.filter(key => key.charAt(0) !== '$');
        const templateKeys = keys.filter(key => key.charAt(0) === '$');

        simpleKeys.forEach(key => {
            const item = extender[key];

            if (typeof item === 'object' && item !== null) {
                base[key] = Array.isArray(item) ? [] : {};
                copyTemplated(base[key], item);
            } else {
                base[key] = item;
            }
        });

        templateKeys.forEach(key => {
            const item = extender[key];

            const val = newContext[item];

            if (val !== undefined) {
                const prop = key.substring(1);
                base[prop] = val;
            }
        });

        return base;
    }

    const newJson = copyTemplated({}, template);

    for (i in json) {
        if (i === 'type' || i === '__proto__') {
            continue;
        }

        if (json.hasOwnProperty(i)) {
            newJson[i] = json[i as keyof typeof json];
        }
    }

    return {
        json: newJson,
        templateContext: newContext
    };
}
