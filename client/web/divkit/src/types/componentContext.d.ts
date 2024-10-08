import type { Readable } from 'svelte/store';
import type { Action, TemplateContext } from '../../typings/common';
import type { MaybeMissing } from '../expressions/json';
import type { Variable, VariableType } from '../expressions/variable';
import type { WrappedError } from '../utils/wrapError';
import type { DivBaseData, Tooltip } from './base';
import type { Store } from '../../typings/store';
import type { evalExpression } from '../expressions/eval';
import type { Node } from '../expressions/ast';

export interface ComponentContext<T extends DivBaseData = DivBaseData> {
    path: string[];
    parent?: ComponentContext;

    json: MaybeMissing<T>;
    origJson?: MaybeMissing<DivBaseData> | undefined;
    templateContext: TemplateContext;
    variables?: Map<string, Variable>;
    isRootState?: boolean;
    fakeElement?: boolean;
    parentContext?: ComponentContext;
    id: string;

    logError(error: WrappedError): void;
    execAnyActions(
        actions: MaybeMissing<Action[]> | undefined,
        opts?: {
            processUrls?: boolean;
            node?: HTMLElement;
            logType?: string;
        }
    ): Promise<void>;
    getDerivedFromVars<T>(
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex?: boolean
    ): Readable<MaybeMissing<T>>;
    getJsonWithVars<T>(
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex?: boolean
    ): MaybeMissing<T>;
    evalExpression(store: Store | undefined, expr: Node, opts?: {
        weekStartDay?: number;
    }): ReturnType<typeof evalExpression>;
    produceChildContext(div: MaybeMissing<DivBaseData>, opts?: {
        path?: string | number | undefined;
        isRootState?: boolean;
        fake?: boolean;
        variables?: Map<string, Variable>;
        id?: string;
        tooltips?: {
            internalId: number;
            ownerNode: HTMLElement;
            desc: MaybeMissing<Tooltip>;
            timeoutId: number | null;
        }[];
    }): ComponentContext;
    getVariable(varName: string, type?: VariableType): Variable | undefined;
}
