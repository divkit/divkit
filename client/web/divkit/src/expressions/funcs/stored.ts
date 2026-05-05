import type { EvalContext, EvalValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import { checkUrl, convertJsValueToDivKit } from '../utils';
import type { StoreScope } from '../../../typings/store';

export function getStored(evalType: 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'array' | 'dict') {
    return (ctx: EvalContext, name: StringValue, scope?: EvalValue, fallback?: EvalValue): EvalValue => {
        const fallbackValue = evalType === DICT || evalType === ARRAY ? undefined : (fallback || scope);
        const scopeValue = (evalType === DICT || evalType === ARRAY || fallback !== undefined) ? scope : undefined;

        if (!ctx.store) {
            if (!fallbackValue) {
                throw new Error('Missing value.');
            }
            return {
                type: evalType,
                value: fallbackValue.value
            } as EvalValue;
        }

        let expectedType: 'boolean' | 'number' | 'string';
        if (evalType === 'boolean') {
            expectedType = 'boolean';
        } else if (evalType === 'number' || evalType === 'integer') {
            expectedType = 'number';
        } else {
            expectedType = 'string';
        }

        let scopeStr: StoreScope | undefined;
        if (scopeValue) {
            if (scopeValue.value === 'global' || scopeValue.value === 'card') {
                scopeStr = scopeValue.value;
            } else {
                throw new Error('Incorrect scope value');
            }
        }

        let val;
        if (ctx.store.get) {
            val = ctx.store.get(name.value, evalType, scopeStr);
        } else if (ctx.store.getValue) {
            val = ctx.store.getValue(name.value, expectedType);
        }

        if (val === undefined) {
            if (!fallbackValue) {
                throw new Error('Missing value.');
            }
            if (evalType === 'url') {
                checkUrl(fallbackValue.value);
            }
            return {
                type: evalType,
                value: fallbackValue.value
            } as EvalValue;
        } else if (evalType === 'url') {
            checkUrl(val);
        }

        return convertJsValueToDivKit(ctx, val, evalType);
    };
}

export function registerStored(): void {
    registerFunc('getStoredIntegerValue', [STRING, INTEGER], getStored(INTEGER));
    registerFunc('getStoredIntegerValue', [STRING, STRING, INTEGER], getStored(INTEGER));
    registerFunc('getStoredNumberValue', [STRING, NUMBER], getStored(NUMBER));
    registerFunc('getStoredNumberValue', [STRING, STRING, NUMBER], getStored(NUMBER));
    registerFunc('getStoredStringValue', [STRING, STRING], getStored(STRING));
    registerFunc('getStoredStringValue', [STRING, STRING, STRING], getStored(STRING));
    registerFunc('getStoredUrlValue', [STRING, URL], getStored(URL));
    registerFunc('getStoredUrlValue', [STRING, STRING, URL], getStored(URL));
    registerFunc('getStoredUrlValue', [STRING, STRING], getStored(URL));
    registerFunc('getStoredUrlValue', [STRING, STRING, STRING], getStored(URL));
    registerFunc('getStoredColorValue', [STRING, COLOR], getStored(COLOR));
    registerFunc('getStoredColorValue', [STRING, STRING, COLOR], getStored(COLOR));
    registerFunc('getStoredColorValue', [STRING, STRING], getStored(COLOR));
    registerFunc('getStoredColorValue', [STRING, STRING, STRING], getStored(COLOR));
    registerFunc('getStoredBooleanValue', [STRING, BOOLEAN], getStored(BOOLEAN));
    registerFunc('getStoredBooleanValue', [STRING, STRING, BOOLEAN], getStored(BOOLEAN));
    registerFunc('getStoredArrayValue', [STRING], getStored(ARRAY));
    registerFunc('getStoredArrayValue', [STRING, STRING], getStored(ARRAY));
    registerFunc('getStoredDictValue', [STRING], getStored(DICT));
    registerFunc('getStoredDictValue', [STRING, STRING], getStored(DICT));
}
