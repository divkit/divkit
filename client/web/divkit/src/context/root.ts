import type { Readable, Writable } from 'svelte/store';
import type { Action, Direction, DisappearAction, DivBase, DivExtension, DivExtensionContext, TemplateContext, TypefaceProvider, VariableTrigger, VisibilityAction } from '../../typings/common';
import type { DivBaseData, Tooltip } from '../types/base';
import type { MaybeMissing } from '../expressions/json';
import type { Variable } from '../expressions/variable';
import type { TintMode } from '../types/image';
import type { Customization } from '../../typings/common';
import type { CustomComponentDescription } from '../../typings/custom';
import type { ComponentContext } from '../types/componentContext';

export const ROOT_CTX = Symbol('root');

export type Running = 'stateChange';

export interface ParentMethods {
    replaceWith: (id: string, items?: DivBase[]) => void;
    isSingleMode: boolean;
}

export interface FocusableMethods {
    focus: () => void;
}

export type ExecAnyActionsFunc = (actions: MaybeMissing<Action[]> | undefined, opts?: {
    componentContext?: ComponentContext;
    processUrls?: boolean;
}) => Promise<void>;

export interface RootCtxValue {
    logStat(type: string, action: MaybeMissing<Action | VisibilityAction | DisappearAction>): void;
    hasTemplate(templateName: string): boolean;
    genId(key: string): string;
    genClass(key: string): string;
    execCustomAction(action: (Action | VisibilityAction | DisappearAction) & { url: string }): void;
    processVariableTriggers(
        componentContext: ComponentContext | undefined,
        variableTriggers: MaybeMissing<VariableTrigger>[] | undefined
    ): (() => void) | undefined;
    isRunning(type: Running): boolean;
    setRunning(type: Running, val: boolean): void;
    registerInstance<T>(id: string, block: T): void;
    unregisterInstance(id: string): void;
    registerParentOf(id: string, methods: ParentMethods): void;
    unregisterParentOf(id: string): void;
    registerTooltip(node: HTMLElement, tooltip: MaybeMissing<Tooltip>): void;
    unregisterTooltip(tooltip: MaybeMissing<Tooltip>): void;
    onTooltipClose(internalId: number): void;
    tooltipRoot: HTMLElement | undefined;
    registerFocusable(id: string, methods: FocusableMethods): void;
    unregisterFocusable(id: string): void;
    addSvgFilter(color: string, mode: TintMode): string;
    removeSvgFilter(color: string | undefined, mode: TintMode): void;
    preparePrototypeVariables(name: string, data: Record<string, unknown>, index: number): Map<string, Variable>;
    getStore<T>(id: string): Writable<T>;
    getCustomization<K extends keyof Customization>(prop: K): Customization[K] | undefined;
    getBuiltinProtocols(): Set<string>;
    getExtension(id: string, params: object | undefined): DivExtension | undefined;
    getExtensionContext(componentContext: ComponentContext): DivExtensionContext;
    registerTimeout(timeout: number): void;
    isPointerFocus: Readable<boolean>;
    typefaceProvider: TypefaceProvider;
    isDesktop: Readable<boolean>;
    direction: Readable<Direction>;
    customComponents: Map<string, CustomComponentDescription> | undefined;

    // Devtool
    componentDevtool?({
        type,
        node,
        json,
        origJson,
        templateContext
    }: {
        type: 'mount' | 'update' | 'destroy';
        node: HTMLElement | null;
        json: MaybeMissing<DivBaseData>;
        origJson: MaybeMissing<DivBaseData> | undefined;
        templateContext: TemplateContext;
    }): void;
}
