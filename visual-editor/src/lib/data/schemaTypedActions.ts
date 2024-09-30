/* eslint-disable @typescript-eslint/no-explicit-any */

import type { TypedAction } from '@divkitframework/divkit/typings/common';
import { schema, type Schema } from './schema';

export interface ControlBase {
    type: string;
    required: boolean;
    name: keyof TypedAction;
    description?: {
        ru: string;
        en: string;
    };
}

export interface StringControl extends ControlBase {
    type: 'string' | 'integer' | 'number' | 'color' | 'url' | 'boolean' | 'array' | 'object';
}

export interface SelectControl extends ControlBase {
    type: 'select';
    options: {
        text: string;
        value: string;
    }[];
    subcontrols: {
        [value: string]: Control[];
    };
}

export type Control = StringControl | SelectControl;

function resolveObject(root: any, obj: any) {
    if (!obj || typeof obj !== 'object') {
        return obj;
    }

    if (typeof obj.$ref === 'string') {
        if (obj.$ref === 'common.json#/non_empty_string') {
            return {
                type: 'string'
            };
        }
        if (obj.$ref === 'common.json#/color') {
            return {
                type: 'color'
            };
        }
        if (obj.$ref === 'common.json#/url') {
            return {
                type: 'url'
            };
        }
        if (obj.$ref.startsWith('#definitions') || obj.$ref.startsWith('#/definitions')) {
            return resolveObject(root, root.definitions[obj.$ref.replace(/^#\/?/, '').split('/').slice(1).join('/')]);
        }
        return resolveSchema(obj.$ref.replace(/\.json$/, ''));
    }

    for (const key in obj) {
        const val = obj[key];

        if (val && typeof val === 'object') {
            if (Array.isArray(val)) {
                for (let i = 0; i < val.length; ++i) {
                    val[i] = resolveObject(root, val[i]);
                }
            } else {
                obj[key] = resolveObject(root, val);
            }
        }
    }

    return obj;
}

function resolveSchema(schemaPath: string): any {
    const json = schema[schemaPath]?.default;

    return resolveObject(json, json);
}

function parseControlsInner(obj: Schema): Control[] {
    const controls: Control[] = [];

    let required = new Set<string>();
    if (Array.isArray(obj.required)) {
        required = new Set(obj.required);
    }

    if (obj.properties) {
        for (const key in obj.properties) {
            const prop = obj.properties[key];
            if (key === 'type' && prop.type === 'string' && Array.isArray(prop.enum) && prop.enum.length === 1) {
                continue;
            }

            if (prop.anyOf) {
                const control: SelectControl = {
                    name: key as keyof TypedAction,
                    type: 'select',
                    options: prop.anyOf.map(item => {
                        const type = item.properties?.type?.enum?.[0];

                        return {
                            text: type,
                            value: type
                        };
                    }).filter(it => it.text) as {
                        text: string;
                        value: string;
                    }[],
                    required: required.has(key),
                    subcontrols: {}
                };

                prop.anyOf.forEach(item => {
                    const type = item.properties?.type?.enum?.[0];

                    if (type) {
                        control.subcontrols[type] = parseControlsInner(item);
                    }
                });

                controls.push(control);
            } else {
                let type: StringControl['type'] = 'string';
                const propType = prop.type;

                if (
                    propType === 'integer' || propType === 'number' ||
                    propType === 'color' || propType === 'url' || propType === 'boolean' ||
                    propType === 'object' || propType === 'array'
                ) {
                    type = propType;
                }

                controls.push({
                    name: key as keyof TypedAction,
                    type,
                    required: required.has(key),
                });
            }
        }
    }

    return controls;
}

export function parseControls(schemaPath: string): Control[] {
    try {
        const schema = resolveSchema(schemaPath);
        if (schema) {
            return parseControlsInner(schema);
        }
    } catch (err) {
        console.error(err);
    }

    return [];
}
