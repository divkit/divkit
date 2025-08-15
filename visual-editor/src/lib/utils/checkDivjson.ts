/* eslint-disable @typescript-eslint/no-explicit-any */

import type { ViewerError } from './errors';

function getTemplateProps(part: unknown): string[] {
    const res: string[] = [];

    if (part && typeof part === 'object') {
        for (const key in part) {
            const val = (part as any)[key];
            if (key.startsWith('$')) {
                if (typeof val === 'string') {
                    res.push(val);
                } else {
                    throw new Error('Template field should be used as string');
                }
            } else if (val && typeof val === 'object') {
                res.push(...getTemplateProps(val));
            }
        }
    }

    return res;
}

export function templatesCheck(json: any): ViewerError[] {
    if (!(json?.templates && typeof json.templates === 'object')) {
        return [];
    }

    const templateProps = new Map<string, string[]>();

    for (const key in json.templates) {
        if (key === '__loc') {
            continue;
        }

        try {
            const template = json.templates[key];
            const props = getTemplateProps((template));
            templateProps.set(key, props);
        } catch (err: any) {
            return [{
                message: err.message,
                stack: (err.stack || '').split('\n'),
                level: 'error'
            }];
        }
    }

    const res: ViewerError[] = [];

    const templateDeps = new Map<string, string[]>();

    const procTemplate = (name: string, template: any): string[] => {
        if (!template || !('type' in template) || typeof template.type !== 'string') {
            throw new Error('Cannot process template without "type"');
        }
        const props = templateProps.get(name);
        if (!props) {
            throw new Error('Wrong template');
        }
        if (template.type in json.templates) {
            return procTemplate(template.type, json.templates[template.type]).concat(props);
        }
        return props;
    };

    for (const key in json.templates) {
        if (key === '__loc') {
            continue;
        }

        try {
            const template = json.templates[key];
            templateDeps.set(key, procTemplate(key, template));
        } catch (err: any) {
            return [{
                message: err.message,
                stack: (err.stack || '').split('\n'),
                level: 'error'
            }];
        }
    }

    const checkTemplate = (name: string, template: any) => {
        const parent = template.type;
        const deps = templateDeps.get(parent);
        if (deps) {
            for (const prop in template) {
                if (prop === 'type') {
                    continue;
                }
                if (deps.includes(prop)) {
                    res.push({
                        message: `Template "${name}" -> property "${prop}": Transitive dependencies is not supported in DivKit for Android and iOS`,
                        stack: [],
                        level: 'error'
                    });
                } else if (prop.startsWith('$') && deps.includes(prop.slice(1))) {
                    res.push({
                        message: `Template "${name}" -> property "${prop.slice(1)}": Transitive dependencies is not supported in DivKit for Android and iOS`,
                        stack: [],
                        level: 'error'
                    });
                }
            }
        }

        for (const key in template) {
            if (template[key] && typeof template[key] === 'object') {
                checkTemplate(name, template[key]);
            }
        }
    };

    for (const key in json.templates) {
        const template = json.templates[key];
        checkTemplate(key, template);
    }

    return res;
}
