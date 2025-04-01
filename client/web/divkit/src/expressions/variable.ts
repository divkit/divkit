import type { Writable, Subscriber, Unsubscriber } from 'svelte/store';
import { writable } from 'svelte/store';
import type { EvalValue } from './eval';
import { parseColor } from '../utils/correctColor';
import { bigIntZero, toBigInt } from './bigint';

export type VariableType = 'string' | 'number' | 'integer' | 'boolean' | 'color' | 'url' | 'dict' | 'array';
export type VariableValue = string | number | bigint | boolean | null | undefined | object | unknown[];

export abstract class Variable<
    ValueType extends ConvertedSetValue = any,
    TypeName = VariableType,
    ConvertedSetValue = ValueType
> {
    protected name: string;
    protected value: ValueType;
    protected store?: Writable<ValueType>;

    constructor(name: string, value: ConvertedSetValue) {
        const val = this.convertValue(value);

        this.name = name;
        this.value = val;
    }

    getName(): string {
        return this.name;
    }

    protected abstract convertValue(value: unknown): ValueType;

    subscribe(cb: Subscriber<ValueType>): Unsubscriber {
        if (!this.store) {
            this.store = writable(this.value);
        }

        return this.store.subscribe(cb);
    }

    set(val: string): void {
        const value = this.fromString(val);

        this.setValue(value);
    }

    protected abstract fromString(val: string): ValueType;

    setValue(val: ConvertedSetValue): void {
        const converted = this.convertValue(val);

        this.value = converted;

        if (this.store) {
            this.store.set(converted);
        }
    }

    getValue(): ValueType {
        return this.value;
    }

    abstract getType(): TypeName;
}

export class StringVariable extends Variable<string, 'string'> {
    protected convertValue(value: unknown) {
        if (typeof value !== 'string') {
            throw new Error('Incorrect variable value');
        }
        return value;
    }

    protected fromString(val: string) {
        return val;
    }

    getType(): 'string' {
        return 'string';
    }
}

export class IntegerVariable extends Variable<bigint, 'integer', bigint | number> {
    protected convertValue(value: unknown) {
        if (typeof value !== 'bigint' && typeof value !== 'number') {
            throw new Error('Incorrect variable value');
        }

        try {
            return toBigInt(value);
        } catch (_err) {
            throw new Error('Incorrect variable value');
        }
    }

    protected fromString(val: string) {
        try {
            return toBigInt(val);
        } catch (_err) {
            throw new Error('Incorrect variable value');
        }
    }

    getType(): 'integer' {
        return 'integer';
    }
}

export class NumberVariable extends Variable<number, 'number'> {
    protected convertValue(value: unknown) {
        if (
            typeof value !== 'number' ||
            Number.isNaN(value) ||
            !isFinite(value)
        ) {
            throw new Error('Incorrect variable value');
        }

        return value;
    }

    protected fromString(val: string) {
        const res = Number(val);

        return this.convertValue(res);
    }

    getType(): 'number' {
        return 'number';
    }
}

export class BooleanVariable extends Variable<number, 'boolean'> {
    protected convertValue(value: unknown) {
        if (value !== 1 && value !== 0 && value !== true && value !== false) {
            throw new Error('Incorrect variable value');
        }

        return Number(value);
    }

    protected fromString(val: string) {
        if (val === '1' || val === 'true') {
            return 1;
        } else if (val === '0' || val === 'false') {
            return 0;
        }

        throw new Error('Incorrect variable value');
    }

    getType(): 'boolean' {
        return 'boolean';
    }
}

export class ColorVariable extends Variable<string, 'color'> {
    protected convertValue(value: unknown) {
        if (typeof value !== 'string' || !parseColor(value)) {
            throw new Error('Incorrect variable value');
        }

        // save input value, some expression tests rely on that
        return value;
    }

    protected fromString(val: string) {
        return this.convertValue(val);
    }

    getType(): 'color' {
        return 'color';
    }
}

export class UrlVariable extends Variable<string, 'url'> {
    protected convertValue(value: unknown) {
        if (typeof value !== 'string') {
            throw new Error('Incorrect variable value');
        }

        return value;
    }

    protected fromString(val: string) {
        return val;
    }

    getType(): 'url' {
        return 'url';
    }
}

export class DictVariable extends Variable<object, 'dict'> {
    protected convertValue(value: unknown) {
        if (typeof value !== 'object' || !value) {
            throw new Error('Incorrect variable value');
        }

        return value;
    }

    protected fromString(val: string): object {
        try {
            return JSON.parse(val);
        } catch (_err) {
            throw new Error('Incorrect dict value');
        }
    }

    getType(): 'dict' {
        return 'dict';
    }
}

export class ArrayVariable extends Variable<unknown[], 'array'> {
    protected convertValue(value: unknown[]) {
        if (!Array.isArray(value)) {
            throw new Error('Incorrect variable value');
        }

        return value;
    }

    protected fromString(val: string): unknown[] {
        try {
            return JSON.parse(val);
        } catch (_err) {
            throw new Error('Incorrect array value');
        }
    }

    getType(): 'array' {
        return 'array';
    }
}

export const TYPE_TO_CLASS: Record<VariableType, typeof Variable<VariableValue, VariableType>> = {
    string: StringVariable,
    number: NumberVariable,
    integer: IntegerVariable,
    boolean: BooleanVariable,
    color: ColorVariable,
    url: UrlVariable,
    dict: DictVariable,
    array: ArrayVariable
};

export function createVariable(
    name: string,
    type: VariableType,
    value: unknown
): InstanceType<typeof TYPE_TO_CLASS[typeof type]> {
    if (!(type in TYPE_TO_CLASS)) {
        throw new Error('Unsupported variable type');
    }

    return new (TYPE_TO_CLASS[type] as any)(name, value);
}

// eslint-disable-next-line @typescript-eslint/no-empty-function
function noop(): void {
}

function constSubscribe<ValueType>(this: Variable<ValueType>, cb: Subscriber<ValueType>): Unsubscriber {
    cb(this.value);

    return noop;
}

function constSetter(): void {
    throw new Error('Cannot change the value of this type of variable');
}

class ConstStringVariable extends StringVariable {}
class ConstNumberVariable extends NumberVariable {}
class ConstIntegerVariable extends IntegerVariable {}
class ConstBooleanVariable extends BooleanVariable {}
class ConstColorVariable extends ColorVariable {}
class ConstUrlVariable extends UrlVariable {}
class ConstDictVariable extends DictVariable {}
class ConstArrayVariable extends ArrayVariable {}

class ConstDatetimeVariable extends Variable<Date, 'datetime'> {
    protected convertValue(value: unknown) {
        if (!(value instanceof Date)) {
            throw new Error('Incorrect variable value');
        }

        return value;
    }

    protected fromString(): never {
        throw new Error('Datetime variable does not support setter from string');
    }

    getType(): 'datetime' {
        return 'datetime';
    }
}

export const CONST_TYPE_TO_CLASS: {
    string: typeof StringVariable;
    number: typeof NumberVariable;
    integer: typeof IntegerVariable;
    boolean: typeof BooleanVariable;
    color: typeof ColorVariable;
    url: typeof UrlVariable;
    dict: typeof DictVariable;
    array: typeof ArrayVariable;
    datetime: typeof ConstDatetimeVariable;
} = {
    string: ConstStringVariable,
    number: ConstNumberVariable,
    integer: ConstIntegerVariable,
    boolean: ConstBooleanVariable,
    color: ConstColorVariable,
    url: ConstUrlVariable,
    dict: ConstDictVariable,
    array: ConstArrayVariable,
    datetime: ConstDatetimeVariable
} as any;

for (const type in CONST_TYPE_TO_CLASS) {
    const Class = CONST_TYPE_TO_CLASS[type as keyof typeof CONST_TYPE_TO_CLASS];

    Class.prototype.subscribe = constSubscribe;
    Class.prototype.set = constSetter;
    Class.prototype.setValue = constSetter;
}

export function createConstVariable<T extends VariableType | 'datetime'>(
    name: string,
    type: T,
    value: unknown
): InstanceType<typeof CONST_TYPE_TO_CLASS[T]> {
    if (!(type in CONST_TYPE_TO_CLASS)) {
        throw new Error('Unsupported variable type');
    }

    return new (CONST_TYPE_TO_CLASS[type] as any)(name, value);
}

export function defaultValueByType(type: keyof typeof TYPE_TO_CLASS): VariableValue {
    if (type === 'integer') {
        return bigIntZero;
    }
    if (type === 'boolean' || type === 'number') {
        return 0;
    }
    if (type === 'dict') {
        return {};
    }
    if (type === 'array') {
        return [];
    }

    return '';
}

export function variableToValue(variable: Variable): EvalValue {
    return {
        type: variable.getType(),
        value: variable.getValue()
    };
}
