/* eslint-disable @typescript-eslint/no-explicit-any */

export function getProp(obj: any, prop: string, fallback?: any): any {
    if (!obj) {
        return fallback;
    }
    let res = obj;
    const parts = prop.split('.');
    for (let i = 0; i < parts.length; ++i) {
        res = res[parts[i]];
        if (!res) {
            break;
        }
    }

    if (res === undefined) {
        return fallback;
    }
    return res;
}

function getAnyProp(obj: any, parentObj: any, prop: string, conditionsContext: Record<string, unknown>): any {
    if (prop.startsWith('$parent.')) {
        if (parentObj) {
            return getProp(parentObj, prop.substring(8));
        }
        return undefined;
    }
    if (prop.startsWith('$')) {
        return conditionsContext[prop];
    }
    return getProp(obj, prop);
}

export interface ConditionEqual {
    prop: string;
    equal: string | number | boolean;
}

export interface ConditionEmpty {
    prop: string;
    isEmpty: true;
}

export interface ConditionOr {
    or: ConditionObject[];
}

export interface ConditionAnd {
    and: ConditionObject[];
}

export interface ConditionNot {
    not: ConditionObject;
}

export type ConditionObject = ConditionEqual | ConditionEmpty | ConditionOr | ConditionAnd | ConditionNot;

export function evalCondition(
    obj: any,
    parentObj: any,
    tree: ConditionObject,
    conditionsContext: Record<string, unknown> = {}
): boolean {
    if ('equal' in tree) {
        const val = getAnyProp(obj, parentObj, tree.prop, conditionsContext);
        return val === tree.equal;
    } else if ('isEmpty' in tree) {
        return getAnyProp(obj, parentObj, tree.prop, conditionsContext) === undefined;
    } else if ('or' in tree) {
        return tree.or.some(it => evalCondition(obj, parentObj, it, conditionsContext));
    } else if ('and' in tree) {
        return tree.and.every(it => evalCondition(obj, parentObj, it, conditionsContext));
    } else if ('not' in tree) {
        return !evalCondition(obj, parentObj, tree.not, conditionsContext);
    }

    throw new Error('Unknown condition');
}
