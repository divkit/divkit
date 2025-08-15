/* eslint-disable @typescript-eslint/no-explicit-any */

import { containerComponents, supportedComponents } from './componentProps';
import { getSchemaByType, resolveAllOf, resolveEditor, resolveObjectRelative, type ComponentProperty, type Schema } from './schema';
import type { State } from './state';

export interface TemplateProperty {
    templatePropertyName: string;
    editor: ComponentProperty;
}

export function getTemplateProps(state: State, template: string): TemplateProperty[] {
    const map = new Map<string, ComponentProperty>();
    let type = template;
    let obj;
    const usedTypes = new Set<string>();
    const provided = new Set<string>();
    const base = state.getBaseType(type);

    if (!base) {
        return [];
    }

    // eslint-disable-next-line no-constant-condition
    while (1) {
        if (usedTypes.has(type)) {
            return [];
        }
        usedTypes.add(type);

        obj = state.userTemplates.get(type);
        if (!obj) {
            break;
        }

        if (type !== template && type !== base) {
            for (const key in obj) {
                if (key !== 'type') {
                    provided.add(key);
                }
            }
        }

        const schemaMod = getSchemaByType(base);
        if (!schemaMod) {
            break;
        }
        const schemaObj = resolveAllOf(schemaMod.default);

        const proc = (obj: unknown, schemaObj: Schema, objType: string) => {
            if (typeof obj === 'object' && obj) {
                for (const key in obj) {
                    let subSchema;
                    const templatedKey = key.startsWith('$') ? key.slice(1) : key;

                    if (schemaObj?.properties && templatedKey in schemaObj.properties) {
                        subSchema = schemaObj.properties[templatedKey];
                    } else if (schemaObj?.allOf) {
                        subSchema = resolveAllOf(schemaObj).properties?.[templatedKey];
                    } else if (schemaObj?.anyOf && 'type' in obj) {
                        subSchema = schemaObj.anyOf
                            .find(it => it.properties?.type?.enum?.[0] === obj.type)
                            ?.properties?.[templatedKey];
                    }

                    if (!subSchema) {
                        return;
                    }

                    const val = (obj as Record<string, unknown>)[key];
                    if (subSchema && objType && key.startsWith('$') && typeof val === 'string') {
                        const propertyEditor = resolveEditor(schemaObj, subSchema);
                        if (propertyEditor) {
                            map.set(val, propertyEditor);
                        }
                    }
                    if (Array.isArray(val)) {
                        const itemSchema = subSchema?.type === 'array' && subSchema.items && resolveObjectRelative(schemaObj, subSchema.items, 0);
                        val.forEach(subitem => {
                            let itemType: string | null = objType;
                            let schema = itemSchema;
                            if (itemSchema?.__isDiv) {
                                itemType = state.getBaseType(subitem.type);
                                if (!itemType) {
                                    return;
                                }
                                const schemaMod = getSchemaByType(itemType);
                                if (!schemaMod) {
                                    return;
                                }
                                schema = resolveAllOf(schemaMod.default);
                            }
                            proc(subitem, schema, itemType);
                        });
                    } else {
                        proc(val, subSchema, objType);
                    }
                }
            }
        };

        proc(obj, schemaObj, base);

        if (typeof obj.type !== 'string') {
            break;
        }

        type = obj.type;
    }

    for (const key in provided) {
        map.delete(key);
    }

    const res: TemplateProperty[] = [];

    for (const [templatePropertyName, editor] of map) {
        res.push({
            templatePropertyName,
            editor
        });
    }

    return res;
}

export function resolveTemplateProps(obj: any, templateProps: Map<string, unknown>): any {
    const proc = (obj: any) => {
        if (!(typeof obj === 'object' && obj)) {
            return obj;
        }

        const res = Array.isArray(obj) ?
            [] as unknown[] :
            {} as Record<string, unknown>;

        for (const key in obj) {
            const isTemplateKey = key.startsWith('$');
            const trimmedKey = isTemplateKey ? key.slice(1) : key;
            const val = isTemplateKey ? templateProps.get(obj[key]) : obj[key];

            res[trimmedKey as keyof typeof res] = isTemplateKey ? val : proc(val);
        }

        return res;
    };

    return proc(obj);
}

export function parseItems(state: State, div: any): {
    childs: any[];
    infos: any[];
    fromDataField?: string;
 } {
    const childs: any[] = [];
    const infos: any[] = [];
    let rootFromDataField: string | undefined;

    const base = state.getBaseType(div.type);
    if (!base || !containerComponents.has(base)) {
        return {
            childs,
            infos
        };
    }

    const templateProps = new Map<string, unknown>();
    const dataItems = new Map<any, string>();
    for (const key in div) {
        if (div[key] && typeof div[key] === 'object') {
            dataItems.set(div[key], key);
        }
    }

    let type = div.type;
    let obj = div;
    const usedTypes = new Set<string>();

    // eslint-disable-next-line no-constant-condition
    while (1) {
        if (usedTypes.has(type)) {
            return {
                childs,
                infos
            };
        }
        usedTypes.add(type);

        if (base !== type) {
            for (const key in obj) {
                if (key !== 'type') {
                    templateProps.set(key, obj[key]);
                }
            }
        }

        if (!state.userTemplates.has(type)) {
            break;
        }

        obj = state.userTemplates.get(type);
        type = obj.type;
    }

    if (!supportedComponents.has(type)) {
        return {
            childs,
            infos
        };
    }

    const schemaMod = getSchemaByType(base);
    if (!schemaMod) {
        return {
            childs,
            infos
        };
    }
    const schemaObj = resolveAllOf(schemaMod.default);

    const proc = (obj: unknown, schemaObj: Schema, objType: string, fromDataField: string) => {
        if (typeof obj === 'object' && obj) {
            for (const key in obj) {
                let subSchema;
                const isTemplateKey = key.startsWith('$');
                const templatedKey = isTemplateKey ? key.slice(1) : key;
                if (schemaObj?.properties && templatedKey in schemaObj.properties) {
                    subSchema = schemaObj.properties[templatedKey];
                }

                const val = isTemplateKey ? templateProps.get(obj[key as keyof typeof obj]) : obj[key as keyof typeof obj];
                const fromData = fromDataField || dataItems.get(val) || '';

                if (!subSchema) {
                    continue;
                }

                if (subSchema?.__isDiv) {
                    if (val && fromData) {
                        childs.push(resolveTemplateProps(val, templateProps));
                        infos.push(resolveTemplateProps(obj, templateProps));
                        rootFromDataField = fromData;
                    }
                    continue;
                }

                if (Array.isArray(val)) {
                    const itemSchema = subSchema?.type === 'array' && subSchema.items && resolveObjectRelative(schemaObj, subSchema.items, 0);
                    val.forEach(subitem => {
                        let itemType: string | null = objType;
                        let schema = itemSchema;
                        if (itemSchema?.__isDiv && fromData) {
                            childs.push(resolveTemplateProps(subitem, templateProps));
                            infos.push(null);
                            rootFromDataField = fromData;
                        } else {
                            if (itemSchema?.__isDiv) {
                                itemType = state.getBaseType(subitem.type);
                                if (itemType) {
                                    const schemaMod = getSchemaByType(itemType);
                                    if (schemaMod) {
                                        schema = resolveAllOf(schemaMod.default);
                                    }
                                }
                            }

                            if (itemType) {
                                proc(subitem, schema, itemType, fromData);
                            }
                        }
                    });
                } else {
                    proc(val, subSchema, objType, fromData);
                }
            }
        }
    };

    const fullObj = {
        ...obj
    };
    for (const [key, val] of templateProps) {
        fullObj[key] = val;
    }

    proc(fullObj, schemaObj, base, '');

    return {
        childs,
        infos,
        fromDataField: rootFromDataField
    };
}

export function isUserTemplateWithoutChilds(state: State, json: any): boolean {
    if (!state.userTemplates.has(json.type)) {
        return false;
    }

    const parsed = parseItems(state, json);
    return !parsed.fromDataField && !parsed.childs.length;
}
