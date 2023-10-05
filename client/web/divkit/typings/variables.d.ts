/* eslint-disable @typescript-eslint/no-empty-interface */

import type { Subscriber, Unsubscriber } from 'svelte/store';

export interface Variable<T = any> {
    getName(): string;
    subscribe(cb: Subscriber<T>): Unsubscriber;
    set(val: string): void;
    setValue(value: T): void;
    setValue(value: T): void;
    getValue(): T;
    getType(): string;
}

export interface StringVariable extends Variable<string> {}
export interface NumberVariable extends Variable<number> {}
export interface IntegerVariable extends Variable<number> {}
export interface BooleanVariable extends Variable<number> {}
export interface ColorVariable extends Variable<string> {}
export interface UrlVariable extends Variable<string> {}
export interface DictVariable extends Variable<object> {}
export interface ArrayVariable extends Variable<unknown[]> {}

export type AnyVariable = StringVariable | NumberVariable | IntegerVariable |
    BooleanVariable | ColorVariable | UrlVariable | DictVariable | ArrayVariable;

export type VariableType = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'dict' | 'array';

export interface GlobalVariablesController {
    setVariable(variable: AnyVariable): void;
    getVariable(variableName: string): AnyVariable | undefined;
}

export function createVariable(variableName: string, type: VariableType, value: unknown): AnyVariable;

export function createGlobalVariablesController(): GlobalVariablesController;
