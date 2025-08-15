/* eslint-disable @typescript-eslint/no-empty-interface */

import type { Subscriber, Unsubscriber } from './common';

export type VariableType = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'dict' | 'array';
export type VariableValue = string | number | bigint | boolean | null | undefined | object | unknown[];

export declare class Variable<
    ValueType extends ConvertedSetValue = any,
    TypeName = VariableType,
    ConvertedSetValue = ValueType
> {
    constructor(name: string, value: ConvertedSetValue);
    getName(): string;
    subscribe(cb: Subscriber<ValueType>): Unsubscriber;
    set(val: string): void;
    setValue(val: ConvertedSetValue): void;
    getValue(): ValueType;
    getType(): TypeName;
}

export declare class StringVariable extends Variable<string, 'string'> {}
export declare class IntegerVariable extends Variable<bigint, 'integer', bigint | number> {}
export declare class NumberVariable extends Variable<number, 'number'> {}
export declare class BooleanVariable extends Variable<number, 'boolean'> {}
export declare class ColorVariable extends Variable<string, 'color'> {}
export declare class UrlVariable extends Variable<string, 'url'> {}
export declare class DictVariable extends Variable<object, 'dict'> {}
export declare class ArrayVariable extends Variable<unknown[], 'array'> {}

declare const TYPE_TO_CLASS: {
    string: typeof StringVariable;
    number: typeof NumberVariable;
    integer: typeof IntegerVariable;
    boolean: typeof BooleanVariable;
    color: typeof ColorVariable;
    url: typeof UrlVariable;
    dict: typeof DictVariable;
    array: typeof ArrayVariable;
};

export type AnyVariable = StringVariable | NumberVariable | IntegerVariable |
    BooleanVariable | ColorVariable | UrlVariable | DictVariable | ArrayVariable;

export interface GlobalVariablesController {
    setVariable(variable: AnyVariable): void;
    getVariable(variableName: string): AnyVariable | undefined;
    list(): IterableIterator<Variable>;
}

export function createVariable<T extends VariableType>(variableName: string, type: T, value: unknown):
    InstanceType<typeof TYPE_TO_CLASS[T]>;

export function createGlobalVariablesController(): GlobalVariablesController;
