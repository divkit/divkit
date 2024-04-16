import type { EvalContext, EvalValue, StringValue } from '../eval';
import { registerFunc } from './funcs';
import { BOOLEAN, COLOR, INTEGER, NUMBER, STRING, URL } from '../const';
import { convertJsValueToDivKit } from '../utils';

export function getStored(evalType: 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url') {
    return (ctx: EvalContext, name: StringValue, fallback: EvalValue): EvalValue => {
        if (!ctx.store) {
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
        const val = ctx.store.getValue(name.value, expectedType);

        if (val === undefined) {
            return {
                type: evalType,
                value: fallback.value
            } as EvalValue;
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
}
