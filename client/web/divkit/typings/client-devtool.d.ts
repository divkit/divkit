import type {
    DivJson,
    StatCallback,
    ErrorCallback,
    DivkitInstance,
    Platform,
    ComponentCallback,
    CustomActionCallback,
    Theme,
    Customization,
    DivExtensionClass,
    TypefaceProvider,
    FetchInit
} from './common';
import type { GlobalVariablesController, Variable } from './variables';
import type { WrappedError } from './common';

export interface DivkitDebugInstance extends DivkitInstance {
    getDebugVariables(): Map<string, Variable>;
}

export function render(opts: {
    target: HTMLElement;
    json: DivJson;
    id: string;
    hydrate?: boolean;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    onStat?: StatCallback;
    onCustomAction?: CustomActionCallback;
    onError?: ErrorCallback;
    onComponent?: ComponentCallback;
    typefaceProvider?: TypefaceProvider;
    platform?: Platform;
    customization?: Customization;
    builtinProtocols?: string[];
    extensions?: Map<string, DivExtensionClass>;
    /** EXPERIMENTAL SUPPORT */
    theme?: Theme;
    fetchInit?: FetchInit;
    tooltipRoot?: HTMLElement;
}): DivkitDebugInstance;

export { createVariable, createGlobalVariablesController } from './variables';

export {
    SizeProvider,
    lottieExtensionBuilder
} from './extensions';

export interface EvalValueBase {
    type: string;
    value: unknown;
}

export interface StringValue extends EvalValueBase {
    type: 'string';
    value: string;
}

export interface UrlValue extends EvalValueBase {
    type: 'url';
    value: string;
}

export interface ColorValue extends EvalValueBase {
    type: 'color';
    value: string;
}

export interface NumberValue extends EvalValueBase {
    type: 'number';
    value: number;
}

export interface IntegerValue extends EvalValueBase {
    type: 'integer';
    value: number;
}

export interface BooleanValue extends EvalValueBase {
    type: 'boolean';
    value: number;
}

export interface DatetimeValue extends EvalValueBase {
    type: 'datetime';
    value: Date;
}

export type EvalValue = StringValue | UrlValue | ColorValue | NumberValue | IntegerValue |
    BooleanValue | DatetimeValue;

export interface EvalError {
    type: 'error';
    value: string;
}

export type EvalResult = EvalValue | EvalError;

export function evalExpression(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): EvalResult;

export function evalExpressionWithFullResult(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): {
    result: EvalResult;
    warnings: WrappedError[];
};

export function valToString(val: EvalValue): string;

export function functionNames(): string[];
