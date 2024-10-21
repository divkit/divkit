import type { Variable } from './expressions/variable';
import type { Node } from './expressions/ast';
import type { ComponentCallback, CustomActionCallback, Customization, DivExtensionClass, DivJson, ErrorCallback, FetchInit, Platform, StatCallback, Theme, TypefaceProvider, WrappedError } from '../typings/common';
import type { GlobalVariablesController } from './expressions/globalVariablesController';
import type { Store } from '../typings/store';
import type { CustomComponentDescription } from '../typings/custom';
import type { DivkitDebugInstance } from '../typings/client-devtool';
import { parse } from './expressions/expressions';
import { evalExpression as evalExpressionInner, type EvalResult } from './expressions/eval';
import { funcs } from './expressions/funcs/funcs';
import Root from './components/Root.svelte';

export function render(opts: {
    target: HTMLElement;
    json: DivJson;
    id: string;
    hydrate?: boolean;
    globalVariablesController?: GlobalVariablesController;
    mix?: string;
    customization?: Customization;
    builtinProtocols?: string[];
    extensions?: Map<string, DivExtensionClass>;
    onStat?: StatCallback;
    onCustomAction?: CustomActionCallback;
    onError?: ErrorCallback;
    onComponent?: ComponentCallback;
    typefaceProvider?: TypefaceProvider;
    platform?: Platform;
    theme?: Theme;
    fetchInit?: FetchInit;
    tooltipRoot?: HTMLElement;
    customComponents?: Map<string, CustomComponentDescription> | undefined;
    store?: Store;
    weekStartDay?: number;
}): DivkitDebugInstance {
    const { target, hydrate, ...rest } = opts;

    const instance = new Root({
        target: target,
        props: rest,
        hydrate: hydrate
    });

    return {
        $destroy() {
            instance.$destroy();
        },
        execAction(action) {
            instance.execAction(action);
        },
        setTheme(theme) {
            instance.setTheme(theme);
        },
        setData(newJson) {
            instance.setData(newJson);
        },
        getDebugVariables() {
            return instance.getDebugVariables();
        },
        getDebugAllVariables() {
            return instance.getDebugAllVariables();
        }
    };
}

export * from './client';

export function evalExpression(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): EvalResult {
    return evalExpressionWithFullResult(expr, opts).result;
}

export function evalExpressionWithFullResult(expr: string, opts?: {
    variables?: Map<string, Variable>;
    type?: 'exact' | 'json';
}): {
    result: EvalResult;
    warnings: WrappedError[];
} {
    let ast: Node;
    try {
        ast = parse(expr, {
            startRule: opts?.type === 'json' ? 'JsonStringContents' : 'start'
        });
    } catch (err) {
        return {
            result: {
                type: 'error',
                value: 'Unable to parse expression'
            },
            warnings: []
        };
    }
    return evalExpressionInner(opts?.variables || new Map(), undefined, ast);
}

export { valToString } from './expressions/utils';

export function functionNames(): string[] {
    return Array.from(funcs.keys());
}

export function parseExpression(expr: string, opts?: {
    type?: 'exact' | 'json';
}): Node {
    return parse(expr, {
        startRule: opts?.type === 'json' ? 'JsonStringContents' : 'start'
    });
}

export { walk as walkExpression } from './expressions/walk';
