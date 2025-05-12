import type { EvalContext, EvalValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { ARRAY, BOOLEAN, COLOR, DICT, INTEGER, NUMBER, STRING, URL } from '../const';
import { checkUrl, convertJsValueToDivKit } from '../utils';

export function getStored(evalType: 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'array' | 'dict') {
    return (ctx: EvalContext, name: StringValue, fallback?: EvalValue): EvalValue => {
        if (!ctx.store) {
            if (!fallback) {
                throw new Error('Missing value.');
            }
            return {
                type: evalType,
                value: fallback.value
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
        let val;
        if (ctx.store.get) {
            val = ctx.store.get(name.value, evalType);
        } else if (ctx.store.getValue) {
            val = ctx.store.getValue(name.value, expectedType);
        }

        if (val === undefined) {
            if (!fallback) {
                throw new Error('Missing value.');
            }
            if (evalType === 'url') {
                checkUrl(fallback.value);
            }
            return {
                type: evalType,
                value: fallback.value
            } as EvalValue;
        } else if (evalType === 'url') {
            checkUrl(val);
        }

        return convertJsValueToDivKit(ctx, val, evalType);
    };
}

export function registerStored(): void {
    registerFunc('getStoredIntegerValue', [STRING, INTEGER], getStored(INTEGER));
    registerFunc('getStoredNumberValue', [STRING, NUMBER], getStored(NUMBER));
    registerFunc('getStoredStringValue', [STRING, STRING], getStored(STRING));
    registerFunc('getStoredUrlValue', [STRING, URL], getStored(URL));
    registerFunc('getStoredUrlValue', [STRING, STRING], getStored(URL));
    registerFunc('getStoredColorValue', [STRING, COLOR], getStored(COLOR));
    registerFunc('getStoredColorValue', [STRING, STRING], getStored(COLOR));
    registerFunc('getStoredBooleanValue', [STRING, BOOLEAN], getStored(BOOLEAN));
    registerFunc('getStoredArrayValue', [STRING], getStored(ARRAY));
    registerFunc('getStoredDictValue', [STRING], getStored(DICT));
}
