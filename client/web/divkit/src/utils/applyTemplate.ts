import { wrapError, type WrappedError } from './wrapError';
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
    const contextValues = templateContext.values || {};
    // Objects that were produced from a template body during an expansion.
    // If such an object is later applied as a template usage, reference values
    // from the outer instance (accumulated in the context values) cascade into it
    // and override the same-named defaults written on the usage site inside the body.
    // Instance-provided data is not tracked here, so its own values win,
    // preserving the scope isolation of nested usages in the instance data.
    const templateBodyContent = templateContext.templateBodyContent || new Set<object>();
    const contextOverridesJson = templateBodyContent.has(json);
    const newValues: Record<string, unknown> = {};

    const copyContextToNewValues = (): void => {
        for (const key in contextValues) {
            if (contextValues.hasOwnProperty(key)) {
                newValues[key] = contextValues[key];
            }
        }
    };

    const copyJsonKeysToNewValues = (): void => {
        for (const key in json) {
            if (key === 'type' || key === '__proto__') {
                continue;
            }

            if (json.hasOwnProperty(key)) {
                newValues[key] = json[key as keyof typeof json];
            }
        }
    };

    if (contextOverridesJson) {
        copyJsonKeysToNewValues();
        copyContextToNewValues();
    } else {
        copyContextToNewValues();
        copyJsonKeysToNewValues();
    }

    function copyTemplated(base: any, extender: any) {
        const keys = Object.keys(extender).filter(key => key !== '__proto__');
        const simpleKeys = keys.filter(key => key.charAt(0) !== '$');
        const templateKeys = keys.filter(key => key.charAt(0) === '$');

        simpleKeys.forEach(key => {
            const item = extender[key];

            if (typeof item === 'object' && item !== null) {
                base[key] = Array.isArray(item) ? [] : {};
                templateBodyContent.add(base[key]);
                copyTemplated(base[key], item);
            } else {
                base[key] = item;
            }
        });

        templateKeys.forEach(key => {
            const item = extender[key];

            const val = newValues[item];

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
        templateContext: {
            values: newValues,
            templateBodyContent
        }
    };
}
