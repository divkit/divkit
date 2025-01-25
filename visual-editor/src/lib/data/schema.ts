/* eslint-disable @typescript-eslint/no-explicit-any */

import { Truthy } from '../utils/truthy';
import { isTemplate, namedTemplates } from './templates';

export interface Schema {
    anyOf?: Schema[];
    allOf?: Schema[];
    $ref?: string;
    $description?: string;
    definitions?: Record<string, Schema>;
    type?: string;
    properties?: Record<string, Schema>;
    default_value?: unknown;
    required?: string[];
    enum?: string[];
    minItems?: number;
    constraint?: string;
    supports_expressions?: boolean;
    items?: Schema;
    additionalProperties?: boolean;
    description?: string;
    format?: string;

    __isDiv?: boolean;
    __isTooltip?: boolean;
    __editor?: string;
}

export type SchemaModule = Schema & {
    default: Schema;
}

const jsons = import.meta.glob('../../../../schema//*.json', {
    eager: true
});
export const schema = Object.keys(jsons).reduce((acc, item) => {
    acc[item.replace('../../../../schema/', '').replace(/\.json$/, '')] = jsons[item] as SchemaModule;
    return acc;
}, {} as Record<string, SchemaModule>);

export function translation(desc: string): Record<string, string> {
    return resolveDescription({}, desc, 0);
}

const editors = new Map([
    ['common.json#/boolean_int', 'boolean'],
    ['common.json#/color', 'color'],
    ['common.json#/url', 'file'],
    ['div-variable-name.json', 'variable-name'],
    ['div-background.json', '__background-item'],
    ['div-action.json', '__action-item'],
    ['div-video-source.json', '__video-source-item']
]);

const arrayEditors = new Map([
    ['__background-item', 'background2'],
    ['__action-item', 'actions2'],
    ['__video-source-item', 'video_sources'],
]);

const resolveLinkCache = new Map<string, any>();
const resolveObjectCache = new Map<string, any>();

function getSchemaItem(name: string) {
    const res = { ...schema[name].default };

    if (name === 'div') {
        res.__isDiv = true;
    }
    if (name === 'div-tooltip') {
        res.__isTooltip = true;
    }

    return res;
}

function transformDesc(desc: string): string {
    return desc
        .replace(/<\/?li>/g, '')
        .replace(/`/g, '"')
        .replace(/\s?\[[^\]]+]\([^)]+\)/g, '');
}

function resolveDescription(rootObj: any, desc: string, count: number): Record<string, string> {
    const resolved = resolveLink(rootObj, desc, count) || {};

    return {
        ru: transformDesc(resolved.ru || ''),
        en: transformDesc(resolved.en || '')
    };
}

export function resolveLink(rootObj: any, ref: string, count: number): any {
    const refParts = ref.split('#');
    const file = refParts[0];
    let relative = refParts[1];

    let root = rootObj;
    const cacheStore: Record<string, unknown> = {};
    const editor = editors.get(ref);
    if (file) {
        const cached = resolveLinkCache.get(ref);
        if (cached) {
            return cached;
        }
        resolveLinkCache.set(ref, cacheStore);
        root = getSchemaItem(file.replace(/\.json$/, ''));
    }

    if (!relative) {
        const res = resolveObjectRelative(root, root, count + 1);
        if (editor) {
            res.__editor = editor;
        }
        if (file) {
            for (const k in res) {
                cacheStore[k] = res[k];
            }
        }
        return res;
    }

    if (!relative.startsWith('/')) {
        relative = '/' + relative;
    }

    let obj2 = root;
    const parts = relative.split('/');
    for (let i = 1; i < parts.length; ++i) {
        const part = parts[i];

        obj2 = obj2?.[part];
    }

    if (obj2) {
        const res = resolveObjectRelative(root, obj2, count + 1);
        if (editor) {
            res.__editor = editor;
        }
        if (file) {
            for (const k in res) {
                cacheStore[k] = res[k];
            }
        }
        return res;
    }
    console.error('Cannot find', ref);
}

export function resolveAnyVal(root: any, val: any, count: number): any {
    if (Array.isArray(val)) {
        return val.map(it => resolveAnyVal(root, it, count + 1));
    } else if (val && typeof val === 'object') {
        return resolveObjectRelative(root, val, count + 1);
    }
    return val;
}

export function resolveObjectRelative(root: any, obj: any, count: number) {
    const stored = resolveObjectCache.get(obj);
    if (stored) {
        return stored;
    }

    let res: any = {};
    let needRebuild = false;
    let cached = false;

    function store() {
        if (!cached) {
            cached = true;
            resolveObjectCache.set(obj, res);
        }
    }

    function rebuild() {
        if (needRebuild) {
            needRebuild = false;
            // he-he
            res = Object.create(res);
        }
    }

    if (obj.$ref) {
        res = resolveLink(root, obj.$ref, count + 1);
        needRebuild = true;
    }
    if (obj.$description) {
        rebuild();
        res.description = resolveDescription(root, obj.$description, count + 1);
    }

    for (const key in obj) {
        if (key === '$ref' || key === '$description') {
            continue;
        }

        rebuild();
        store();
        res[key] = resolveAnyVal(root, obj[key], count + 1);
    }

    store();

    return res;
}

const resolveCache = new Map<string, any>();
export function resolveDesc(name: string) {
    const cached = resolveCache.get(name);
    if (cached) {
        return cached;
    }
    const desc = schema[name].default;
    if (desc) {
        const res = resolveObject(desc);
        if (res) {
            resolveCache.set(name, res);
            return res;
        }
    }
    console.error('Cannot resolve', name);
}

export function resolveObject(obj: any) {
    return resolveObjectRelative(obj, obj, 0);
}

export function filterProp<T extends Record<string, unknown>>(obj: T, prop: string): Partial<T> {
    return (Object.keys(obj) as (keyof T)[]).reduce((acc, item) => {
        if (item !== prop) {
            acc[item] = obj[item];
        }
        return acc;
    }, {} as Partial<T>);
}

interface PropItemBase {
    type: string;
}

interface PropItemList extends PropItemBase {
    type: 'list';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    options: null;
    list: PropItem[];
    subtype: string | undefined;
    description: string | undefined;
}

interface PropItemGroup extends PropItemBase {
    type: 'group';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    options: {
        name: string;
        value: string;
    }[] | null;
    subprops: PropItem[] | undefined;
    description: string | undefined;
    additionalProperties?: boolean | undefined;
}

interface PropItemSelect extends PropItemBase {
    type: 'select';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    options: {
        name: string;
        value: string;
    }[];
    description: string | undefined;
}

interface PropItemCheckbox extends PropItemBase {
    type: 'checkbox';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    description: string | undefined;
}

interface PropItemColor extends PropItemBase {
    type: 'color';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    description: string | undefined;
}

interface PropItemText extends PropItemBase {
    type: 'text';
    name: string;
    desc: Schema;
    prop: string;
    value: unknown;
    flags?: {
        subtype: 'integer' | 'number' | undefined;
        constraint: string | undefined;
    };
    description: string | undefined;
}

export type PropItem = PropItemList | PropItemGroup | PropItemSelect | PropItemCheckbox | PropItemColor | PropItemText;

export function getPropsList(desc: any, componentJson: any, subpath = '', mergeAllOf = false): PropItem[] | undefined {
    let desc2 = desc;
    if (desc.allOf && mergeAllOf) {
        desc2 = resolveAllOf(desc2);
    }
    // eslint-disable-next-line no-nested-ternary
    const props = desc2.allOf ?
        desc2.allOf[desc2.allOf.length - 1].properties :
        (desc2.anyOf ? {} : desc2.properties);

    if (!props) {
        return;
    }

    return Object.keys(props).filter(it => {
        const res = !(it === 'type' && props[it].type === 'string' && Array.isArray(props[it].enum) && props[it].enum.length === 1) &&
            !(props[it].type === 'array' && props[it].items?.__isDiv) &&
            !(props[it].__isDiv);

        return res;
    }).map(it => {
        const res = getPropsElement(it, props[it], componentJson?.[it], subpath + it);

        if (!res) {
            // todo support payload (action -> payload / extension -> params)
        }

        return res;
    }).filter(Truthy);
}

export function resolveAllOf(prop: any): Schema {
    let res = prop;

    if (prop?.allOf) {
        res = {};
        [...prop.allOf, filterProp(prop, 'allOf')].forEach(it => {
            let obj = it;

            if (obj.$ref) {
                obj = resolveObjectRelative(prop, obj, 0);
            }

            const properties = res.properties && obj.properties ?
                { ...res.properties, ...obj.properties } :
                res.properties || obj.properties;

            res = {
                ...res,
                ...obj,
                properties
            };
        });
    }

    return res;
}

export function getPropsElement(name: string, prop: Schema, componentJson: any, subpath = ''): PropItem | undefined {
    if (!prop) {
        return;
    }

    prop = resolveAllOf(prop);

    const res = prop.type === 'text' ||
        prop.type === 'string' ||
        prop.type === 'number' ||
        prop.type === 'integer' ||
        prop.type === 'array' ||
        prop.type === 'object' && (prop.properties || prop.additionalProperties) ||
        prop.anyOf;

    if (!res) {
        return;
    }

    let subtype: 'integer' | 'number' | undefined;
    let constraint: string | undefined;

    if (prop.type === 'integer') {
        subtype = 'integer';
    } else if (prop.type === 'number') {
        subtype = 'number';
    }
    if (prop.constraint) {
        constraint = prop.constraint;
    }

    // todo deprecated

    if (prop.type === 'array' && prop.items) {
        let list = componentJson;
        const itemDesc = resolveAllOf(prop.items) as Schema;

        if (list) {
            const resolvedAnyOf = itemDesc.anyOf;

            list = Array.isArray(list) ? list.map((it2, index) => {
                if (resolvedAnyOf) {
                    const selected = resolvedAnyOf.find(it3 =>
                        resolveAllOf(it3)?.properties?.type?.enum?.[0] === it2.type
                    );

                    const list: object[] = [{
                        type: 'select',
                        name: 'type',
                        desc: prop,
                        prop: subpath + '[' + index + '].type',
                        value: it2.type,
                        options: resolvedAnyOf.map(it => {
                            const resolved = resolveAllOf(it);
                            const type = resolved?.properties?.type?.enum?.[0];

                            return {
                                name: type,
                                value: type
                            };
                        }),
                        // todo desc
                        description: prop.description
                    }];

                    if (selected) {
                        const sublist = getPropsList(selected, it2, subpath + '[' + index + '].', true);
                        if (sublist) {
                            list.push(...sublist);
                        }
                    }

                    return list;
                } else if (itemDesc.type === 'object') {
                    return getPropsList(itemDesc, it2, subpath + '[' + index + '].', true);
                }
                return [getPropsElement('item', itemDesc, it2, subpath + '[' + index + ']')];
            }) : [];
        }

        const subtype = itemDesc.type;

        return {
            type: 'list',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            options: null,
            list,
            subtype,
            description: prop.description
        };
    }

    if (prop.type === 'object') {
        const subprops = getPropsList(prop, componentJson, subpath + '.', true);

        return {
            type: 'group',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            options: null,
            subprops,
            description: prop.description,
            additionalProperties: prop.additionalProperties
        };
    }

    if (prop.anyOf) {
        let subprops;

        const resolvedAnyOf = prop.anyOf;

        if (componentJson?.type) {
            const selected = resolvedAnyOf.find(it2 =>
                it2?.properties?.type?.enum?.[0] === componentJson.type
            );

            if (selected) {
                subprops = getPropsList(selected, componentJson, subpath + '.', true);
            }
        }

        return {
            type: 'group',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            options: resolvedAnyOf.map(it => {
                it = resolveAllOf(it);

                const type = it?.properties?.type;

                if (
                    type && type.type === 'string' && Array.isArray(type.enum) && type.enum.length === 1 && typeof type.enum[0] === 'string'
                ) {
                    return {
                        name: type.enum[0],
                        value: type.enum[0]
                    };
                }
                console.error('Unknown format', it);
            }).filter(Truthy),
            subprops,
            description: prop.description
        };
    }

    if (prop.type === 'string' && prop.enum) {
        return {
            type: 'select',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            options: prop.enum.map(it => ({
                name: it,
                value: it
            })),
            description: prop.description
        };
    }

    if (prop.format === 'boolean') {
        return {
            type: 'checkbox',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            description: prop.description
        };
    }

    if (prop.format === 'color') {
        return {
            type: 'color',
            name,
            desc: prop,
            prop: subpath,
            value: componentJson,
            description: prop.description
        };
    }

    return {
        type: 'text',
        name,
        desc: prop,
        prop: subpath,
        value: componentJson,
        flags: {
            subtype,
            constraint
        },
        description: prop.description
    };
}

export interface ComponentProperty {
    type: string;
    constraint?: string;
    options?: {
        name?: string;
        rawName?: string;
        value: string | number;
    }[];
    supportsExpressions?: boolean;
}

export const resolveEditor = (root: any, schemaItem: Schema): ComponentProperty | undefined => {
    let obj = schemaItem;
    if (schemaItem?.$ref) {
        const editor = editors.get(schemaItem.$ref);
        if (editor) {
            return {
                type: editor
            };
        }
        obj = resolveLink(root, schemaItem.$ref, 0);
    }

    if (obj.type === 'string' && Array.isArray(obj.enum) && obj.enum.length > 1) {
        return {
            type: 'select',
            options: obj.enum.map(it => ({
                rawName: it,
                value: it
            }))
        };
    } else if (
        (
            obj.type === 'string' ||
            obj.type === 'integer' ||
            obj.type === 'number'
        ) &&
        !(obj.type === 'string' && Array.isArray(obj.enum) && obj.enum.length === 1)
    ) {
        return {
            type: obj.type,
            constraint: obj.constraint,
            supportsExpressions: obj.supports_expressions ?? true
        };
    } else if (obj.type === 'array') {
        let items = obj.items;
        if (items?.$ref) {
            items = resolveLink(root, items.$ref, 0);
        }
        if (items?.__editor && arrayEditors.has(items.__editor)) {
            return {
                type: arrayEditors.get(items.__editor) as string
            };
        }
    }
};

export const componentTypeToSchema = new Map<string, string>();
export const schemaToComponentType = new Map<string, string>();
const SIMPLE_TYPES: Set<string> = new Set();
(getSchemaItem('div').anyOf || []).forEach(it => {
    const resolved = resolveAllOf(resolveObject(it));
    const type = resolved?.properties?.type?.enum?.[0];
    const schemaName = it.$ref?.replace(/\.json$/, '');

    if (type && schemaName) {
        componentTypeToSchema.set(type, schemaName);
        schemaToComponentType.set(schemaName, type);

        let hasItems = false;
        const visited = new Set();
        const proc = (obj: any, path: string, isArray: boolean): void => {
            if (!obj) {
                return;
            }

            if (Array.isArray(obj)) {
                obj.forEach((it, index) => {
                    proc(it, path + '.' + index, true);
                });
            } else if (typeof obj === 'object') {
                if (obj.__isTooltip) {
                    // skip tooltips
                    return;
                }
                if (obj.__isDiv) {
                    hasItems = true;
                    return;
                }

                if (visited.has(obj)) {
                    return;
                }
                visited.add(obj);
                for (const key in obj) {
                    proc(obj[key], path + '.' + key, isArray);
                }
            }
        };

        proc(resolved, 'root', false);

        if (!hasItems) {
            SIMPLE_TYPES.add(type);
        }
    }
});

export function getSchemaNameByType(type: string): string | undefined {
    return componentTypeToSchema.get(type)?.replace(/^div-/, '');
}

export function getTypeBySchemaName(schemaName: string): string | undefined {
    return schemaToComponentType.get(schemaName);
}

export function getSchemaByType(type: string) {
    const schemaName = componentTypeToSchema.get(type);
    if (!schemaName) {
        console.error(`Unknown type ${type}`);
        return;
    }
    return schema[schemaName];
}

export function isSimpleElement(type: string): boolean {
    if (isTemplate(type)) {
        return SIMPLE_TYPES.has(namedTemplates[type].baseType || '');
    }
    return SIMPLE_TYPES.has(type);
}

for (const key in namedTemplates) {
    const obj = namedTemplates[key];
    let type = obj.template.type;
    while (typeof type === 'string' && !componentTypeToSchema.has(type)) {
        type = namedTemplates[type]?.template?.type;
    }
    if (typeof type === 'string' && type) {
        obj.baseType = type;
    } else {
        console.error('unknown base type', key);
    }
}
