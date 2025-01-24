<script lang="ts" context="module">
    import { type Readable, type Writable, writable } from 'svelte/store';

    let isPointerFocus = writable(true);
    let rootInstancesCount = 0;

    function onWindowKeyDown() {
        isPointerFocus.set(false);
    }

    function onWindowPointerDown() {
        isPointerFocus.set(true);
    }

    const AVAIL_SET_STORED_TYPES = new Set(['string', 'integer', 'number', 'url', 'color', 'boolean']);
    const AVAIL_SET_STORED_ALL_TYPES = new Set(['string', 'integer', 'number', 'url', 'color', 'boolean', 'array', 'dict']);
</script>

<script lang="ts">
    import { onDestroy, onMount, setContext, tick } from 'svelte';
    import { derived } from 'svelte/store';

    import css from './Root.module.css';

    import type {
        Action,
        VisibilityAction,
        ComponentCallback,
        CustomActionCallback,
        DivBase,
        DivJson,
        ErrorCallback,
        Platform,
        StatCallback,
        TemplateContext,
        Theme,
        Customization,
        DivExtension,
        DivExtensionContext,
        DivExtensionClass,
        TypefaceProvider,
        DisappearAction,
        FetchInit,
        DivVariable,
        Direction,
        ActionMenuItem,
        VariableTrigger,
        DownloadCallbacks
    } from '../../typings/common';
    import type { CustomComponentDescription } from '../../typings/custom';
    import type { Animator, AppearanceTransition, DivBaseData, DivFunction, Tooltip, TransitionChange } from '../types/base';
    import type { SwitchElements, Overflow } from '../types/switch-elements';
    import type { TintMode } from '../types/image';
    import type { VideoElements } from '../types/video';
    import type { Patch } from '../types/patch';
    import type { ComponentContext } from '../types/componentContext';
    import type { Store, StoreAllTypes, StoreTypes } from '../../typings/store';
    import Unknown from './utilities/Unknown.svelte';
    import RootSvgFilters from './utilities/RootSvgFilters.svelte';
    import { ROOT_CTX, type FocusableMethods, type NodeGetter, type ParentMethods, type RootCtxValue, type Running } from '../context/root';
    import { applyTemplate } from '../utils/applyTemplate';
    import { type LogError, wrapError, type WrappedError } from '../utils/wrapError';
    import { checkCustomFunction, customFunctionWrap, mergeCustomFunctions, type CustomFunctions } from '../expressions/funcs/customFuncs';
    import { simpleCheckInput } from '../utils/simpleCheckInput';
    import { ACTION_CTX, type ActionCtxValue } from '../context/action';
    import { STATE_CTX, type StateCtxValue, type StateInterface } from '../context/state';
    import { constStore } from '../utils/constStore';
    import {
        type MaybeMissing,
        prepareVars
    } from '../expressions/json';
    import { storesMap } from '../stores';
    import { evalExpression } from '../expressions/eval';
    import { parse } from '../expressions/expressions';
    import { gatherVarsFromAst } from '../expressions/utils';
    import { Truthy } from '../utils/truthy';
    import { createConstVariable, createVariable, TYPE_TO_CLASS, Variable, type VariableType } from '../expressions/variable';
    import {
        cleanControllerStore,
        getControllerStore,
        getControllerVars,
        GlobalVariablesController
    } from '../expressions/globalVariablesController';
    import { getUrlSchema, isBuiltinSchema } from '../utils/url';
    import { TimersController } from '../utils/timers';
    import { arrayInsert, arrayRemove, arraySet } from '../actions/array';
    import { dictSetValue } from '../actions/dict';
    import { copyToClipboard } from '../actions/copyToClipboard';
    import { filterEnabledActions } from '../utils/filterEnabledActions';
    import { ENABLED_CTX, type EnabledCtxValue } from '../context/enabled';
    import { createAnimator, type AnimatorInstance } from '../utils/animators';
    import { getStateContext, getTooltipContext } from '../utils/componentUtils';
    import TooltipView from './tooltip/Tooltip.svelte';
    import Menu from './menu/Menu.svelte';

    export let id: string;
    export let json: Partial<DivJson> = {};
    export let platform: Platform = 'auto';
    export let theme: Theme = 'system';
    export let globalVariablesController: GlobalVariablesController | undefined = undefined;
    export let mix = '';
    export let customization: Customization = {};
    export let builtinProtocols: string[] = ['http', 'https', 'tel', 'mailto', 'intent'];
    export let extensions: Map<string, DivExtensionClass> = new Map();
    export let onError: ErrorCallback | undefined = undefined;
    export let onStat: StatCallback | undefined = undefined;
    export let onCustomAction: CustomActionCallback | undefined = undefined;
    export let onComponent: ComponentCallback | undefined = undefined;
    export let typefaceProvider: TypefaceProvider = _fontFamily => '';
    export let fetchInit: FetchInit = {};
    export let tooltipRoot: HTMLElement | undefined = undefined;
    export let customComponents: Map<string, CustomComponentDescription> | undefined = undefined;
    export let direction: Direction = 'ltr';
    export let store: Store | undefined = undefined;
    export let weekStartDay = 0;

    let isMounted = true;

    let isDesktop = writable(platform === 'desktop');
    if (platform === 'auto' && typeof matchMedia !== 'undefined') {
        const touchQuery = matchMedia('(any-pointer: coarse)');
        isDesktop.set(!touchQuery.matches);
        touchQuery.addListener(() => {
            isDesktop.set(!touchQuery.matches);
        });
    }

    let currentTheme: 'light' | 'dark' = 'light';
    let themeQuery: MediaQueryList | null = null;
    $: if (theme === 'light' || theme === 'dark') {
        currentTheme = theme;
    } else if (theme === 'system') {
        if (typeof matchMedia !== 'undefined') {
            if (!themeQuery) {
                themeQuery = matchMedia('(prefers-color-scheme: dark)');
                themeQuery.addListener(themeQueryListener);
            }
            currentTheme = themeQuery.matches ? 'dark' : 'light';
        } else {
            currentTheme = 'light';
        }
    } else {
        logError(wrapError(new Error('Unsupported theme')));
    }

    $: if (currentTheme) {
        updateTheme();
    }

    const directionStore = writable<Direction>(direction === 'rtl' ? 'rtl' : 'ltr');

    function themeQueryListener(): void {
        if (theme !== 'system' || !themeQuery) {
            return;
        }

        currentTheme = themeQuery.matches ? 'dark' : 'light';
    }

    export function setTheme(newTheme: Theme): void {
        theme = newTheme;
    }

    export function getDebugVariables() {
        if (!process.env.DEVTOOL) {
            return new Map<string, Variable>();
        }

        return localVariables;
    }

    export function getDebugAllVariables() {
        if (!process.env.DEVTOOL) {
            return new Map<string, Variable>();
        }

        return variables;
    }

    export function setData(newJson: Partial<DivJson>) {
        json = newJson;
    }

    const builtinSet = new Set(builtinProtocols);

    let hasError = false;
    let hsaIdError = false;

    $: {
        hasError = false;

        const initialError = simpleCheckInput(json);
        if (initialError) {
            hasError = true;
            logError(initialError);
        }
    }

    if (!id) {
        hsaIdError = true;
        logError(wrapError(new Error('"id" prop is required')));
    }

    $: templates = json.templates || {};

    const running: Record<Running, boolean> = {
        stateChange: false
    };

    const variablesController = globalVariablesController || new GlobalVariablesController();

    // Will notify about new global variables
    const globalVariablesStore = getControllerStore(variablesController);
    // Global variables only
    const globalVariables = getControllerVars(variablesController);
    // Local variables only
    const localVariables = new Map<string, Variable>();
    // Local and global variables combined, with local in precedence
    const variables = new Map<string, Variable>();
    // Stores for notify unset global variables
    const awaitingGlobalVariables = new Map<string, Writable<any>>();

    let timersController: TimersController | null = null;

    const animators: Map<string, AnimatorInstance> = new Map();

    let tooltipCounter = 0;
    let tooltips: {
        internalId: number;
        ownerNode: HTMLElement;
        desc: MaybeMissing<Tooltip>;
        timeoutId: number | null;
        componentContext: ComponentContext | undefined;
    }[] = [];
    const shownTooltips = new Set<string>();
    let menu: {
        items: MaybeMissing<ActionMenuItem>[];
        node: HTMLElement;
        componentContext: ComponentContext | undefined;
    } | undefined;

    const timeouts: number[] = [];

    function mergeMaps<T>(
        variables0: Map<string, T>,
        variables1: Map<string, T> | undefined
    ): Map<string, T>;
    function mergeMaps<T>(
        variables0: Map<string, T> | undefined,
        variables1: Map<string, T> | undefined
    ): Map<string, T> | undefined;
    function mergeMaps<T>(
        variables0: Map<string, T> | undefined,
        variables1: Map<string, T> | undefined
    ): Map<string, T> | undefined {
        if (variables0 && variables1) {
            return new Map([...variables0, ...variables1]);
        } else if (variables0) {
            return variables0;
        } else if (variables1) {
            return variables1;
        }

        return undefined;
    }

    function getCustomization<K extends keyof Customization>(prop: K): Customization[K] | undefined {
        return customization?.[prop];
    }

    function getDerivedFromVars<T>(
        logError: LogError,
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex = false,
        customFunctions: CustomFunctions | undefined = undefined
    ): Readable<MaybeMissing<T>> {
        if (!jsonProp) {
            return constStore(jsonProp);
        }

        const vars = mergeMaps(variables, additionalVars);

        const prepared = prepareVars(jsonProp, logError, store, weekStartDay);
        if (!prepared.vars.length) {
            if (prepared.hasExpression) {
                return constStore(prepared.applyVars(vars, customFunctions));
            }
            return constStore(jsonProp);
        }
        const stores = prepared.vars.map(name => {
            return vars.get(name) || awaitVariableChanges(name);
        }).filter(Truthy);

        return derived(stores, () => prepared.applyVars(vars, customFunctions, keepComplex));
    }

    function getJsonWithVars<T>(
        logError: LogError,
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex = false,
        customFunctions: CustomFunctions | undefined = undefined
    ): MaybeMissing<T> {
        const prepared = prepareVars(jsonProp, logError, store, weekStartDay);

        if (!prepared.hasExpression) {
            return jsonProp;
        }

        const vars = mergeMaps(variables, additionalVars);

        return prepared.applyVars(vars, customFunctions, keepComplex);
    }

    function preparePrototypeVariables(
        name: string,
        data: Record<string, unknown>,
        index: number
    ): Map<string, Variable> {
        const map = new Map<string, Variable>();

        const dict = createConstVariable(name, 'dict', data);
        map.set(name, dict);

        const indexVar = createConstVariable('index', 'integer', index);
        map.set('index', indexVar);

        return map;
    }

    function logError(error: WrappedError): void {
        if (onError) {
            onError({
                error
            });
        } else if (error?.level === 'warn') {
            console.warn(error);
        } else {
            console.error(error);
        }
    }

    function logStat(type: string, action: Action): void {
        if (onStat) {
            onStat({
                type,
                action
            });
        }
    }

    function hasTemplate(templateName: string): boolean {
        return templateName in templates;
    }

    function processTemplate(json: MaybeMissing<DivBaseData>, templateContext: TemplateContext): {
        json: MaybeMissing<DivBaseData>;
        templateContext: TemplateContext;
    } {
        if (!json) {
            return {
                json,
                templateContext
            };
        }

        const usedTypes = new Set([json.type]);

        while (json.type && json.type in templates) {
            ({
                json,
                templateContext
            } = applyTemplate(json as DivBase, templateContext, templates, logError));

            if (usedTypes.has(json.type)) {
                return {
                    json,
                    templateContext
                };
            }
            usedTypes.add(json.type);
        }

        return {
            json,
            templateContext
        };
    }

    function componentDevtoolReal({
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
    }): void {
        if (onComponent) {
            onComponent({
                type,
                node,
                json: json as DivBase,
                origJson: origJson as DivBase | undefined,
                templateContext
            });
        }
    }

    let idCounter = 0;
    function genId(key: string): string {
        if (process.env.IS_PROD) {
            return `${id}-${idCounter++}`;
        }
        return `${id}-${key}-${idCounter++}`;
    }

    function genClass(key: string): string {
        return `divkit-${genId(key)}`;
    }

    let svgFiltersMap: Record<string, string> = {};
    let svgFilterUsages: Record<string, number> = {};

    function addSvgFilter(color: string, mode: TintMode): string {
        const key = `${color}:${mode}`;
        svgFilterUsages[key] = svgFilterUsages[key] || 0;
        ++svgFilterUsages[key];

        if (svgFiltersMap[key]) {
            return svgFiltersMap[key];
        }

        const filterId = `${genId('root')}-svg-filter`;
        svgFiltersMap = {
            ...svgFiltersMap,
            [key]: filterId
        };

        return filterId;
    }

    function removeSvgFilter(color: string | undefined, mode: TintMode): void {
        if (!color) {
            return;
        }

        const key = `${color}:${mode}`;
        if (!svgFilterUsages[key]) {
            return;
        }

        if (--svgFilterUsages[key] === 0) {
            svgFiltersMap = Object.keys(svgFiltersMap).reduce((acc, item) => {
                if (svgFilterUsages[item]) {
                    acc[item] = svgFiltersMap[item];
                }
                return acc;
            }, {} as typeof svgFiltersMap);
        }
    }

    const idPrefix = genId('byid') + '-id-';
    const nodeGettersById = new Map<string, NodeGetter>();
    const nodeById = new Map<string, HTMLElement>();

    function fullId(id: string): string {
        return idPrefix + id;
    }

    function registerId(id: string, getter: NodeGetter): void {
        nodeGettersById.set(id, getter);
    }

    function unregisterId(id: string): void {
        nodeGettersById.delete(id);

        const full = fullId(id);

        if (nodeById.has(full)) {
            nodeById.delete(full);
        }
    }

    function getComponentId(id: string): string {
        const node = nodeGettersById.get(id)?.();

        if (node) {
            const full = fullId(id);
            const prev = nodeById.get(full);

            if (prev && prev !== node) {
                prev.removeAttribute('id');
            }
            node.setAttribute('id', full);
            nodeById.set(full, node);

            return full;
        }

        return '';
    }

    async function setState(
        stateId: string | null | undefined,
        componentContext: ComponentContext | undefined
    ): Promise<void> {
        if (!stateId) {
            throw new Error('Missing state id');
        }

        let parts = stateId.split('/');
        const tooltipCtx = parts.length % 2 === 0 && getTooltipContext(componentContext);
        let ctx: ComponentContext | undefined = tooltipCtx || rootComponentContext;

        if (!tooltipCtx) {
            if (ctx.states?.root) {
                ctx = await ctx.states.root(parts[0]);
                if (!ctx) {
                    return;
                }
                parts = parts.slice(1);
            } else {
                return;
            }
        }

        for (let i = 0; i < parts.length; i += 2) {
            const divId = parts[i];
            const selectedStateId = parts[i + 1];

            if (ctx.states?.[divId]) {
                ctx = await ctx.states?.[divId](selectedStateId);
                if (!ctx) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    function switchElementAction(
        type: 'set_current_item' | 'set_previous_item' | 'set_next_item' | 'scroll_to_start' |
            'scroll_to_end' | 'scroll_backward' | 'scroll_forward' | 'scroll_to_position',
        id: string | null,
        {
            item,
            step,
            overflow
        }: {
            item?: string | null;
            step?: string | null;
            overflow?: string | null;
        }
    ): void {
        if (!id) {
            throw new Error(`Missing id for "${type}" action`);
        }

        const itemVal = Number(item);
        if (type === 'set_current_item' && Number.isNaN(itemVal)) {
            throw new Error(`Incorrect item for "${type}" action`);
        }

        let stepVal = Number(step);
        if (!step && (type === 'set_previous_item' || type === 'set_next_item')) {
            stepVal = 1;
        }
        if (
            !step && (type === 'scroll_backward' || type === 'scroll_forward' || type === 'scroll_to_position') ||
            Number.isNaN(stepVal)
        ) {
            throw new Error(`Incorrect step value for "${type}" action`);
        }

        if (overflow && overflow !== 'clamp' && overflow !== 'ring') {
            throw new Error(`Incorrect overflow value for "${type}" action`);
        }
        overflow = overflow || 'clamp';

        const instance = getInstance<SwitchElements>(id);
        if (!instance) {
            return;
        }

        switch (type) {
            case 'set_current_item':
                instance.setCurrentItem(itemVal);
                return;
            case 'set_previous_item':
                instance.setPreviousItem(stepVal, overflow as Overflow);
                return;
            case 'set_next_item':
                instance.setNextItem(stepVal, overflow as Overflow);
                return;
            case 'scroll_to_start':
                instance.scrollToStart?.();
                return;
            case 'scroll_to_end':
                instance.scrollToEnd?.();
                return;
            case 'scroll_backward':
                instance.scrollBackward?.(stepVal, overflow as Overflow);
                return;
            case 'scroll_forward':
                instance.scrollForward?.(stepVal, overflow as Overflow);
                return;
            case 'scroll_to_position':
                instance.scrollToPosition?.(stepVal);
                return;
        }
    }

    function callVideoAction(
        id: string | null | undefined,
        action: string | null | undefined,
        componentContext?: ComponentContext
    ): void {
        const log = (componentContext?.logError || logError);

        if (id) {
            const instance = getInstance<VideoElements>(id);

            if (instance) {
                if (action === 'start') {
                    instance.start();
                } else if (action === 'pause') {
                    instance.pause();
                } else {
                    log(wrapError(new Error('Unknown video action'), {
                        additional: {
                            id,
                            action
                        }
                    }));
                }
            } else {
                log(wrapError(new Error('Video component is not found'), {
                    additional: {
                        id,
                        action
                    }
                }));
            }
        } else {
            log(wrapError(new Error('Missing id in video action'), {
                additional: {
                    action
                }
            }));
        }
    }

    function callDownloadAction(
        url: string | null | undefined,
        callbacks: MaybeMissing<DownloadCallbacks | undefined>,
        componentContext?: ComponentContext
    ): void {
        const log = (componentContext?.logError || logError);

        if (url) {
            let init;
            if (typeof fetchInit === 'function') {
                init = fetchInit(url);
            } else {
                init = fetchInit;
            }
            fetch(url, init).then(res => {
                if (!res.ok) {
                    throw new Error('Response is not ok');
                }
                return res.json();
            }).then((json: Patch) => {
                if (!json) {
                    log(wrapError(new Error('Incorrect patch'), {
                        additional: {
                            url
                        }
                    }));
                    return;
                }
                if (json.templates) {
                    for (const name in json.templates) {
                        if (!templates.hasOwnProperty(name)) {
                            templates[name] = json.templates[name];
                        }
                    }
                }
                if (Array.isArray(json.patch?.changes)) {
                    if (json.patch.mode === 'transactional') {
                        const failed = json.patch.changes.find(change => {
                            const methods = parentOfMap.get(change.id);
                            if (!methods) {
                                return true;
                            }
                            const newItemsLen = Array.isArray(change.items) ? change.items.length : 0;
                            if (methods.isSingleMode && newItemsLen !== 1) {
                                return true;
                            }
                            return false;
                        });
                        if (failed) {
                            log(wrapError(new Error('Skipping transactional, child is not found or broken'), {
                                additional: {
                                    url,
                                    id: failed.id
                                }
                            }));
                            execAnyActions(json.patch?.on_failed_actions);
                            execAnyActions(callbacks?.on_fail_actions);
                            return;
                        }
                    }
                    json.patch.changes.forEach(change => {
                        const methods = parentOfMap.get(change.id);
                        if (methods) {
                            methods.replaceWith(change.id, change.items);
                        }
                    });
                    execAnyActions(json.patch?.on_applied_actions);
                    execAnyActions(callbacks?.on_success_actions);
                }
            }).catch(err => {
                log(wrapError(new Error('Failed to download the patch'), {
                    additional: {
                        url,
                        originalError: err
                    }
                }));
                execAnyActions(callbacks?.on_fail_actions);
            });
        } else {
            log(wrapError(new Error('Missing url in download action'), {
                additional: {
                    url
                }
            }));
        }
    }

    function callShowTooltip(
        id: string | null | undefined,
        multiple: string | boolean | null | undefined,
        componentContext?: ComponentContext
    ): void {
        const log = (componentContext?.logError || logError);

        if (!id) {
            log(wrapError(new Error('Missing id in show_tooltip action')));
            return;
        }
        const item = tooltipMap.get(id);
        if (!item) {
            log(wrapError(new Error('Tooltip with the provided id is not found'), {
                additional: {
                    id
                }
            }));
            return;
        }
        if ((multiple !== 'true' && multiple !== true) && shownTooltips.has(id)) {
            return;
        }
        shownTooltips.add(id);

        const info = {
            internalId: ++tooltipCounter,
            ownerNode: item.onwerNode,
            desc: item.tooltip,
            timeoutId: 0,
            componentContext
        };
        tooltips = [...tooltips, info];

        const duration = item.tooltip.duration ?? 5000;
        if (duration) {
            info.timeoutId = window.setTimeout(() => {
                info.timeoutId = 0;
                tooltips = tooltips.filter(it => it.internalId !== info.internalId);
            }, duration);
        }
    }

    function callHideTooltip(id: string | null | undefined, componentContext?: ComponentContext): void {
        const log = (componentContext?.logError || logError);

        if (!id) {
            log(wrapError(new Error('Missing id in hide_tooltip action')));
            return;
        }
        tooltips = tooltips.filter(it => {
            const res = it.desc.id !== id;

            if (!res && it.timeoutId) {
                clearTimeout(it.timeoutId);
                it.timeoutId = null;
            }

            return res;
        });
    }

    function callSetStoredValue(
        componentContext: ComponentContext | undefined,
        name: string | null | undefined,
        value: object | string | bigint | number | boolean | null | undefined,
        type: string | null | undefined,
        lifetime: string | number | null | undefined
    ): void {
        const log = componentContext?.logError || logError;
        if (!store) {
            log(wrapError(new Error('Store is not configured')));
            return;
        }

        let val = value;

        if (!name || !val || !type || !lifetime) {
            log(wrapError(new Error('Missing required params for set_stored_value')));
            return;
        }
        if (!AVAIL_SET_STORED_ALL_TYPES.has(type)) {
            log(wrapError(new Error('Incorrect stored type')));
            return;
        }

        if (type === 'boolean') {
            val = val === 'true' || val === '1';
        }

        if (store.set) {
            store.set(name, type as StoreAllTypes, val, Number(lifetime));
        } else if (store.setValue) {
            if (!AVAIL_SET_STORED_TYPES.has(type)) {
                log(wrapError(new Error('Incorrect stored type')));
                return;
            }
            if (typeof val !== 'string' && typeof val !== 'number' && typeof val !== 'boolean') {
                log(wrapError(new Error('Incorrect stored value')));
                return;
            }
            if (type === 'integer' || type === 'number') {
                val = Number(val);
            }
            store.setValue(name, type as StoreTypes, val, Number(lifetime));
        }
    }

    export function execAction(action: MaybeMissing<Action | VisibilityAction | DisappearAction>): void {
        execActionInternal(getJsonWithVars(logError, action));
    }

    async function execActionInternal(
        action: MaybeMissing<Action | VisibilityAction | DisappearAction>,
        componentContext?: ComponentContext
    ): Promise<void> {
        const scopeId = action.scope_id;
        const log = (componentContext?.logError || logError);

        if (scopeId) {
            const set = componentContextMap.get(scopeId);
            if (set && set?.size > 1) {
                log(wrapError(new Error(`Ambiguous scope id. There are ${set.size} divs with id '${scopeId}'`), {
                    additional: {
                        count: set.size,
                        scopeId
                    }
                }));
            } else if (set?.size === 1) {
                const first = set.values().next().value;
                if (first) {
                    componentContext = first;
                }
            }
        }

        const actionUrl = action.url ? String(action.url) : '';
        const actionTyped = action.typed;

        if (!filterEnabledActions(action)) {
            return;
        }

        if (actionUrl) {
            try {
                const url = actionUrl.replace(/div-action:\/\//, '');
                const parts = /([^?]+)\?(.+)/.exec(url);
                if (!parts) {
                    return;
                }
                const params = new URLSearchParams(parts[2]);

                switch (parts[1]) {
                    case 'set_state':
                        await setState(params.get('state_id'), componentContext);
                        break;
                    case 'set_current_item':
                    case 'set_previous_item':
                    case 'set_next_item':
                    case 'scroll_to_start':
                    case 'scroll_to_end':
                    case 'scroll_backward':
                    case 'scroll_forward':
                    case 'scroll_to_position':
                        switchElementAction(parts[1], params.get('id'), {
                            item: params.get('item'),
                            step: params.get('step'),
                            overflow: params.get('overflow')
                        });
                        break;
                    case 'set_variable':
                        const name = params.get('name');
                        const value = params.get('value');

                        if (name && value !== null) {
                            const variableInstance = componentContext?.getVariable(name) || variables.get(name);
                            if (variableInstance) {
                                const type = variableInstance.getType();
                                if (type === 'dict' || type === 'array') {
                                    log(wrapError(new Error(`Setting ${type} variables is not supported`), {
                                        additional: {
                                            name
                                        }
                                    }));
                                } else {
                                    variableInstance.set(value);
                                }
                            } else {
                                log(wrapError(new Error('Cannot find variable'), {
                                    additional: {
                                        name
                                    }
                                }));
                            }
                        } else {
                            log(wrapError(new Error('Incorrect set_variable_action'), {
                                additional: {
                                    url
                                }
                            }));
                        }
                        break;
                    case 'timer':
                        const timerAction = params.get('action');
                        const id = params.get('id');

                        if (timersController) {
                            timersController.execTimerAction(id, timerAction);
                        } else {
                            log(wrapError(new Error('Incorrect timer action'), {
                                additional: {
                                    id,
                                    action: timerAction
                                }
                            }));
                        }
                        break;
                    case 'video':
                        callVideoAction(params.get('id'), params.get('action'), componentContext);
                        break;
                    case 'download':
                        callDownloadAction(params.get('url'), action.download_callbacks, componentContext);
                        break;
                    case 'show_tooltip':
                        callShowTooltip(params.get('id'), params.get('multiple'), componentContext);
                        break;
                    case 'hide_tooltip':
                        callHideTooltip(params.get('id'), componentContext);
                        break;
                    case 'set_stored_value': {
                        callSetStoredValue(componentContext, params.get('name'), params.get('value'), params.get('type'), params.get('lifetime'));
                        break;
                    }
                    default:
                        log(wrapError(new Error('Unknown type of action'), {
                            additional: {
                                url: actionUrl
                            }
                        }));
                }
            } catch (err: any) {
                log(wrapError(err, {
                    additional: {
                        url: actionUrl
                    }
                }));
            }
        } else if (actionTyped) {
            switch (actionTyped.type) {
                case 'set_variable': {
                    const { variable_name: name, value } = actionTyped;
                    if (name && value) {
                        const variableInstance = componentContext?.getVariable(name) || variables.get(name);
                        if (variableInstance) {
                            const type = variableInstance.getType();
                            if (type === value.type) {
                                variableInstance.setValue(value.value);
                            } else {
                                log(wrapError(new Error('Trying to set value with invalid type'), {
                                    additional: {
                                        name,
                                        type: value.type
                                    }
                                }));
                            }
                        } else {
                            log(wrapError(new Error('Cannot find variable'), {
                                additional: {
                                    name
                                }
                            }));
                        }
                    } else {
                        log(wrapError(new Error('Incorrect set_variable action'), {
                            additional: {
                                name
                            }
                        }));
                    }
                    break;
                }
                case 'array_insert_value':
                    arrayInsert(componentContext, variables, log, actionTyped);
                    break;
                case 'array_remove_value':
                    arrayRemove(componentContext, variables, log, actionTyped);
                    break;
                case 'array_set_value':
                    arraySet(componentContext, variables, log, actionTyped);
                    break;
                case 'copy_to_clipboard':
                    copyToClipboard(log, actionTyped);
                    break;
                case 'focus_element': {
                    const methods = actionTyped.element_id && focusableMap.get(actionTyped.element_id);
                    if (methods) {
                        methods.focus();
                    } else {
                        log(wrapError(new Error('Incorrect focus_element action'), {
                            additional: {
                                elementId: actionTyped.element_id
                            }
                        }));
                    }
                    break;
                }
                case 'clear_focus': {
                    try {
                        if (document.activeElement instanceof HTMLElement) {
                            document.activeElement.blur();
                        }
                    } catch (err) {
                        // do nothing
                    }
                    break;
                }
                case 'dict_set_value': {
                    dictSetValue(componentContext, variables, log, actionTyped);
                    break;
                }
                case 'animator_start': {
                    const animatorDef = actionTyped.animator_id &&
                        componentContext?.getAnimator(actionTyped.animator_id);

                    if (!animatorDef) {
                        log(wrapError(new Error('Missing animator'), {
                            additional: {
                                animator_id: actionTyped.animator_id
                            }
                        }));

                        return;
                    }

                    const {
                        duration,
                        start_delay,
                        interpolator,
                        direction,
                        repeat_count,
                        start_value: start_value_typed,
                        end_value: end_value_typed
                    } = actionTyped;

                    const evalledDef = componentContext ?
                        componentContext.getJsonWithVars(animatorDef) :
                        getJsonWithVars(logError, animatorDef);

                    const props = {
                        ...evalledDef,
                        end_actions: animatorDef.end_actions,
                        cancel_actions: animatorDef.cancel_actions,
                        duration: duration !== undefined ? duration : evalledDef.duration,
                        start_delay: start_delay !== undefined ? start_delay : evalledDef.start_delay,
                        interpolator: interpolator !== undefined ? interpolator : evalledDef.interpolator,
                        direction: direction !== undefined ? direction : evalledDef.direction,
                        repeat_count: repeat_count !== undefined ? repeat_count : evalledDef.repeat_count,
                        start_value_typed,
                        end_value_typed
                    };

                    const instance = animatorDef.variable_name &&
                        (
                            componentContext?.getVariable(animatorDef.variable_name) ||
                            variables.get(animatorDef.variable_name)
                        );
                    if (!instance) {
                        return;
                    }

                    const prevAnimator = animators.get(animatorDef.id as string);
                    if (prevAnimator) {
                        prevAnimator.stop();
                    }

                    const animator = createAnimator(props, instance, () => {
                        animators.delete(animatorDef.id as string);
                    }, (actions, opts) => {
                        const fn = componentContext?.execAnyActions || execAnyActions;
                        const evalled = componentContext ?
                            componentContext.getJsonWithVars(actions) :
                            getJsonWithVars(logError, actions);

                        return fn(evalled, opts);
                    });
                    if (animator) {
                        animators.set(animatorDef.id as string, animator);
                    }

                    break;
                }
                case 'animator_stop': {
                    const animator = animators.get(actionTyped.animator_id as string);
                    if (animator) {
                        animator.stop();
                        animators.delete(actionTyped.animator_id as string);
                    }

                    break;
                }
                case 'show_tooltip': {
                    callShowTooltip(actionTyped.id, actionTyped.multiple, componentContext);
                    break;
                }
                case 'hide_tooltip': {
                    callHideTooltip(actionTyped.id, componentContext);
                    break;
                }
                case 'timer': {
                    if (timersController) {
                        timersController.execTimerAction(actionTyped.id, actionTyped.action);
                    } else {
                        log(wrapError(new Error('Incorrect timer action'), {
                            additional: {
                                id: actionTyped.id,
                                action: actionTyped.action
                            }
                        }));
                    }
                    break;
                }
                case 'download': {
                    callDownloadAction(actionTyped.url, actionTyped, componentContext);
                    break;
                }
                case 'video': {
                    callVideoAction(actionTyped.id, actionTyped.action, componentContext);
                    break;
                }
                case 'set_stored_value': {
                    callSetStoredValue(
                        componentContext,
                        actionTyped.name,
                        actionTyped.value?.value,
                        actionTyped.value?.type,
                        actionTyped.lifetime
                    );
                    break;
                }
                case 'set_state': {
                    await setState(actionTyped.state_id, componentContext);
                    break;
                }
                default: {
                    log(wrapError(new Error('Unknown type of action'), {
                        additional: {
                            type: actionTyped.type
                        }
                    }));
                }
            }
        }
    }

    async function execAnyActions(
        actions: MaybeMissing<Action[]> | undefined,
        opts: {
            componentContext?: ComponentContext;
            processUrls?: boolean;
            node?: HTMLElement;
            logType?: string;
        } = {}
    ): Promise<void> {
        if (!actions || !Array.isArray(actions)) {
            return;
        }

        const filtered = actions.filter(filterEnabledActions);

        for (let i = 0; i < filtered.length; ++i) {
            let action = filtered[i];

            const actionUrl = action.url;
            const actionTyped = action.typed;
            if (actionUrl) {
                const schema = getUrlSchema(actionUrl);
                if (schema) {
                    if (isBuiltinSchema(schema, builtinSet)) {
                        if (opts.processUrls) {
                            if (action.target === '_blank') {
                                const win = window.open('', '_blank');
                                if (win) {
                                    win.opener = null;
                                    win.location = actionUrl;
                                }
                            } else {
                                location.href = actionUrl;
                            }
                        }
                    } else if (schema === 'div-action') {
                        await execActionInternal(action, opts.componentContext);
                        await tick();
                    } else if (action.log_id) {
                        execCustomAction(action as Action & { url: string });
                        await tick();
                    }
                }
            } else if (actionTyped) {
                await execActionInternal(action, opts.componentContext);
            } else if (opts.node && Array.isArray(action.menu_items) && action.menu_items.length) {
                menu = {
                    items: action.menu_items,
                    node: opts.node,
                    componentContext: opts.componentContext
                };
            }
        }
        filtered.forEach(action => {
            if (action.log_id) {
                logStat(opts.logType || 'click', action as Action);
            }
        });
    }

    function execCustomAction(action: (Action | VisibilityAction) & { url: string }): void {
        onCustomAction?.(action);
    }

    function processVariableTriggers(
        componentContext: ComponentContext | undefined,
        variableTriggers: MaybeMissing<VariableTrigger>[] | undefined
    ): (() => void) | undefined {
        const log = componentContext?.logError || logError;

        if (!Array.isArray(variableTriggers) || !variableTriggers.length) {
            return;
        }
        if (!process.env.ENABLE_EXPRESSIONS) {
            log(wrapError(new Error('variable_trigger is not supported')));
            return;
        }

        const list: (() => void)[] = [];

        variableTriggers.forEach(trigger => {
            let prevConditionResult = false;

            if (typeof trigger.condition !== 'string') {
                log(wrapError(new Error('variable_trigger has a condition that is not a string'), {
                    additional: {
                        condition: trigger.condition
                    }
                }));
                return;
            }

            if (!Array.isArray(trigger.actions)) {
                log(wrapError(new Error('variable_trigger has no actions'), {
                    additional: {
                        condition: trigger.condition
                    }
                }));
                return;
            }

            const mode = trigger.mode || 'on_condition';

            if (mode !== 'on_variable' && mode !== 'on_condition') {
                log(wrapError(new Error('variable_trigger has an unsupported mode'), {
                    additional: {
                        mode
                    }
                }));
                return;
            }

            try {
                const ast = parse(trigger.condition, {
                    startRule: 'JsonStringContents'
                });
                const exprVars = gatherVarsFromAst(ast);
                if (!exprVars.length) {
                    log(wrapError(new Error('variable_trigger must have variables in the condition'), {
                        additional: {
                            condition: trigger.condition
                        }
                    }));
                    return;
                }

                const stores = exprVars.map(name =>
                    componentContext?.getVariable(name) ||
                        variables.get(name) ||
                        awaitVariableChanges(name)
                );

                const unsubscribe = derived(stores, () => {
                    const res = componentContext ?
                        componentContext.evalExpression(store, ast, {
                            weekStartDay
                        }) :
                        evalExpression(variables, undefined, store, ast, {
                            weekStartDay
                        });

                    res.warnings.forEach(logError);

                    return res.result;
                }).subscribe(async conditionResult => {
                    if (conditionResult.type === 'error') {
                        log(wrapError(new Error('variable_trigger condition execution error'), {
                            additional: {
                                message: conditionResult.value
                            }
                        }));
                        return;
                    }

                    if (
                        // if condition is truthy
                        conditionResult.value &&
                        // and trigger mode matches
                        (mode === 'on_variable' || mode === 'on_condition' && prevConditionResult === false)
                    ) {
                        prevConditionResult = Boolean(conditionResult.value);
                        const actions = (trigger.actions as Action[]).map(action =>
                            componentContext ?
                                componentContext.getJsonWithVars(action) :
                                getJsonWithVars(logError, action)
                        );

                        if (componentContext) {
                            await componentContext.execAnyActions(actions, {
                                logType: 'trigger'
                            });
                        } else {
                            await execAnyActions(actions, {
                                logType: 'trigger'
                            });
                        }
                    } else {
                        prevConditionResult = Boolean(conditionResult.value);
                    }
                });

                list.push(unsubscribe);
            } catch (err) {
                log(wrapError(new Error('Unable to parse variable_trigger'), {
                    additional: {
                        condition: trigger.condition
                    }
                }));
            }
        });

        return () => {
            list.forEach(cb => {
                cb();
            });
        };
    }

    function isRunning(type: Running): boolean {
        return running[type];
    }

    function setRunning(type: Running, val: boolean): void {
        running[type] = val;
    }

    const instancesMap: Map<string, unknown> = new Map();
    const parentOfMap: Map<string, ParentMethods> = new Map();
    const focusableMap: Map<string, FocusableMethods> = new Map();
    const tooltipMap: Map<string, {
        onwerNode: HTMLElement;
        tooltip: MaybeMissing<Tooltip>;
    }> = new Map();
    const componentContextMap: Map<string, Set<ComponentContext>> = new Map();
    function registerInstance<T>(id: string, block: T) {
        if (instancesMap.has(id)) {
            logError(wrapError(new Error('Duplicate instance id'), {
                additional: {
                    id
                }
            }));
            return;
        }

        instancesMap.set(id, block);
    }
    function unregisterInstance(id: string) {
        instancesMap.delete(id);
    }

    function getInstance<T>(id: string): T | undefined {
        if (!instancesMap.has(id)) {
            logError(wrapError(new Error('Missing instance with id'), {
                additional: {
                    id
                }
            }));
            return;
        }

        return instancesMap.get(id) as T;
    }

    function registerParentOf(id: string, methods: ParentMethods): void {
        parentOfMap.set(id, methods);
    }

    function unregisterParentOf(id: string): void {
        parentOfMap.delete(id);
    }

    function registerFocusable(id: string, methods: FocusableMethods): void {
        focusableMap.set(id, methods);
    }

    function unregisterFocusable(id: string): void {
        focusableMap.delete(id);
    }

    function registerTooltip(onwerNode: HTMLElement, tooltip: MaybeMissing<Tooltip>): void {
        const id = tooltip.id;

        if (!id) {
            return;
        }

        if (tooltipMap.has(id)) {
            logError(wrapError(new Error('Duplicate tooltip id'), {
                additional: {
                    id
                }
            }));
        }

        tooltipMap.set(id, {
            onwerNode,
            tooltip
        });
    }

    function unregisterTooltip(tooltip: MaybeMissing<Tooltip>): void {
        const id = tooltip.id;

        if (!id) {
            return;
        }

        tooltipMap.delete(id);

        if (tooltips.some(it => it.desc.id === id)) {
            tooltips = tooltips.filter(it => it.desc.id !== id);
        }
    }

    const stores = new Map<string, Writable<any>>();

    function setStore(id: string) {
        if (stores.has(id)) {
            return;
        }

        stores.set(id, storesMap[id]());
    }

    function getStore<T>(id: string): Writable<T> {
        if (!stores.has(id)) {
            setStore(id);
        }

        return stores.get(id) as Writable<T>;
    }

    function awaitVariableChanges(variableName: string): Readable<any> {
        const store = awaitingGlobalVariables.get(variableName) || writable(undefined);

        if (!awaitingGlobalVariables.has(variableName)) {
            awaitingGlobalVariables.set(variableName, store);
        }

        return store;
    }

    function updateTheme(): void {
        if (!palette) {
            return;
        }

        const list = palette[currentTheme];
        list.forEach(item => {
            const varInstance = variables.get(item.name);

            if (varInstance) {
                varInstance.setValue(item.color);
            }
        });
    }

    function getBuiltinProtocols(): Set<string> {
        return builtinSet;
    }

    function getExtension(id: string, params: object | undefined): DivExtension | undefined {
        const Builder = extensions.get(id);
        if (Builder) {
            return new Builder(params || {});
        }
    }

    function getExtensionContext(componentContext: ComponentContext): DivExtensionContext {
        return {
            variables: mergeMaps(variables, componentContext.variables),
            processExpressions: function<T>(t: T) {
                return getJsonWithVars(
                    logError,
                    t
                ) as T;
            },
            execAction,
            logError,
            getComponentProperty: function<T>(property: string): T {
                return componentContext.getJsonWithVars((componentContext.json as any)[property]) as T;
            },
            direction
        };
    }

    function produceComponentContext(from?: ComponentContext | undefined): ComponentContext {
        const ctx: ComponentContext = {
            id: '',
            json: {} as DivBaseData,
            path: [],
            templateContext: {},
            logError(error) {
                error.additional = error.additional || {};
                error.additional.path = ctx.path.join('/');
                if (process.env.DEVTOOL) {
                    error.additional.json = ctx.json;
                    error.additional.origJson = ctx.origJson;

                    const fullpath: ComponentContext[] = [];
                    let temp = ctx;
                    while (temp.parent) {
                        fullpath.push(temp);
                        temp = temp.parent;
                    }
                    error.additional.fullpath = fullpath;
                }
                logError(error);
            },
            execAnyActions(actions, opts = {}) {
                return execAnyActions(actions, {
                    componentContext: ctx,
                    processUrls: opts.processUrls,
                    node: opts.node,
                    logType: opts.logType
                });
            },
            getDerivedFromVars(jsonProp, additionalVars, keepComplex = false) {
                return getDerivedFromVars(
                    ctx.logError,
                    jsonProp,
                    mergeMaps(ctx.variables, additionalVars),
                    keepComplex,
                    ctx.customFunctions
                );
            },
            getJsonWithVars(jsonProp, additionalVars, keepComplex = false) {
                return getJsonWithVars(
                    ctx.logError,
                    jsonProp,
                    mergeMaps(ctx.variables, additionalVars),
                    keepComplex,
                    ctx.customFunctions
                );
            },
            evalExpression(store, expr, opts) {
                return evalExpression(mergeMaps(variables, ctx.variables), ctx.customFunctions, store, expr, opts);
            },
            produceChildContext(div, opts = {}) {
                const componentContext = produceComponentContext(ctx);

                let childJson: MaybeMissing<DivBaseData> = div;
                let childContext: TemplateContext = ctx.templateContext;

                const {
                    templateContext: childProcessedContext,
                    json: childProcessedJson
                } = processTemplate(childJson, childContext);

                componentContext.json = childProcessedJson;
                componentContext.templateContext = childProcessedContext;
                componentContext.origJson = div;
                componentContext.id = opts.id || childProcessedJson.id || '';

                if (componentContext.id) {
                    let set = componentContextMap.get(componentContext.id);
                    if (!set) {
                        set = new Set();
                        componentContextMap.set(componentContext.id, set);
                    }

                    set.add(componentContext);
                }

                if (opts.path !== undefined/*  && !res.isRootState */) {
                    componentContext.path.push(String(opts.path));
                }
                if (div.type && !opts.isRootState) {
                    componentContext.path.push(div.type);
                }
                if (opts.isTooltipRoot) {
                    componentContext.isTooltipRoot = true;
                }

                let localVars: Map<string, Variable> | undefined;

                if (Array.isArray(childProcessedJson.variables)) {
                    localVars = new Map();
                    childProcessedJson.variables.forEach(desc => {
                        const varInstance = constructVariable(desc);
                        if (varInstance && localVars) {
                            localVars.set(varInstance.getName(), varInstance);
                        }
                    });
                }
                componentContext.variables = mergeMaps(
                    ctx.variables,
                    mergeMaps(localVars, opts.variables)
                );

                let localCustomFunctions: CustomFunctions | undefined;
                if (Array.isArray(childProcessedJson.functions)) {
                    localCustomFunctions = new Map();
                    childProcessedJson.functions.forEach(desc => {
                        if (localCustomFunctions) {
                            try {
                                checkCustomFunction(desc);
                            } catch (err: unknown) {
                                // Only Error thrown here
                                ctx.logError(wrapError(err as Error));
                                return;
                            }
                            const fn = desc as DivFunction;
                            const list = localCustomFunctions.get(fn.name) || [];
                            list.push(customFunctionWrap(fn));
                            localCustomFunctions.set(fn.name, list);
                        }
                    });
                }
                componentContext.customFunctions = mergeCustomFunctions(ctx.customFunctions, localCustomFunctions);

                if (Array.isArray(childProcessedJson.animators)) {
                    ctx.animators = childProcessedJson.animators.reduce<Record<string, MaybeMissing<Animator>>>(
                        (acc, item) => {
                            if (item.id) {
                                acc[item.id] = item;
                            }
                            return acc;
                        },
                        {}
                    );
                }

                if (opts.fake) {
                    componentContext.fakeElement = true;
                }
                if (opts.isRootState) {
                    componentContext.isRootState = true;
                }

                return componentContext;
            },
            getVariable(varName, type) {
                const variable = ctx.variables?.get(varName) || variables.get(varName);

                if (variable) {
                    const foundType = variable.getType();

                    if (type && foundType !== type) {
                        ctx.logError(wrapError(new Error(`Variable should have type "${type}"`), {
                            additional: {
                                name: varName,
                                foundType
                            }
                        }));
                        return;
                    }
                }

                return variable;
            },
            getAnimator(name) {
                return ctx.animators?.[name] || ctx.parent?.getAnimator(name) || undefined;
            },
            registerState(stateId, setState) {
                const stateCtx = getStateContext(ctx.parent);

                if (stateCtx) {
                    stateCtx.states = stateCtx.states || {};
                    stateCtx.states[stateId] = setState;
                }
            },
            unregisterState(stateId) {
                const stateCtx = getStateContext(ctx.parent);

                if (stateCtx?.states) {
                    delete stateCtx.states[stateId];
                }
            },
            destroy() {
                const set = componentContextMap.get(ctx.id);
                if (set) {
                    set.delete(ctx);
                    if (!set.size) {
                        componentContextMap.delete(ctx.id);
                    }
                }
            },
        };

        if (from) {
            ctx.parent = from;
            ctx.path = from.path.slice();

            if (from.fakeElement) {
                ctx.fakeElement = true;
            }
        } else {
            ctx.json = {
                type: 'root'
            };
            ctx.isRootState = true;
        }

        return ctx;
    }

    function registerTimeout(timeout: number): void {
        if (isMounted) {
            timeouts.push(timeout);
        } else {
            clearTimeout(timeout);
        }
    }

    setContext<RootCtxValue>(ROOT_CTX, {
        logStat,
        hasTemplate,
        genId,
        genClass,
        execCustomAction,
        processVariableTriggers,
        isRunning,
        setRunning,
        registerInstance,
        unregisterInstance,
        registerParentOf,
        unregisterParentOf,
        registerTooltip,
        unregisterTooltip,
        onTooltipClose,
        tooltipRoot,
        registerFocusable,
        unregisterFocusable,
        addSvgFilter,
        removeSvgFilter,
        registerId,
        unregisterId,
        getComponentId,
        preparePrototypeVariables,
        getStore,
        getCustomization,
        getBuiltinProtocols,
        getExtension,
        getExtensionContext,
        registerTimeout,
        typefaceProvider,
        isDesktop,
        isPointerFocus,
        customComponents,
        direction: directionStore,
        componentDevtool: process.env.DEVTOOL ? componentDevtoolReal : undefined
    });

    setContext<ActionCtxValue>(ACTION_CTX, {
        hasAction(): boolean {
            return false;
        }
    });

    setContext<StateCtxValue>(STATE_CTX, {
        runVisibilityTransition(
            _json: DivBaseData,
            _componentContext: ComponentContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement,
            _direction: 'in' | 'out'
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionIn(
            _json: DivBaseData,
            _componentContext: ComponentContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionOut(
            _json: DivBaseData,
            _componentContext: ComponentContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionChange(
            _json: DivBaseData,
            _componentContext: ComponentContext,
            _transitions: TransitionChange,
            _node: HTMLElement
        ) {
            return Promise.resolve();
        },
        hasTransitionChange(_id?: string): boolean {
            return false;
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        registerChild(_id: string) {
        },
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        unregisterChild(_id: string) {
        }
    });

    setContext<EnabledCtxValue>(ENABLED_CTX, {
        isEnabled: constStore(true)
    });

    function hasVariableWithType(name: string, type: VariableType): boolean {
        const instance = variables.get(name);

        return instance?.getType() === type;
    }

    function setVariableValue(name: string, value: unknown): void {
        const variableInstance = variables.get(name);
        if (variableInstance) {
            variableInstance.setValue(value);
        } else {
            logError(wrapError(new Error('Cannot find variable'), {
                additional: {
                    name
                }
            }));
        }
    }

    function constructVariable(variable: MaybeMissing<DivVariable>): Variable | undefined {
        if (!variable.type || !variable.name || !(variable.type in TYPE_TO_CLASS)) {
            // Skip unknown types (from the future versions maybe)
            return;
        }

        if (
            variable.type === 'integer' && typeof variable.value === 'number' &&
            (variable.value > Number.MAX_SAFE_INTEGER || variable.value < Number.MIN_SAFE_INTEGER)
        ) {
            logError(wrapError(new Error('The value of the integer variable could lose accuracy'), {
                level: 'warn',
                additional: {
                    name: variable.name,
                    value: variable.value
                }
            }));
        }

        try {
            return createVariable(variable.name, variable.type, variable.value);
        } catch (err: any) {
            logError(wrapError(err, {
                additional: {
                    name: variable.name
                }
            }));
        }
    }

    function declVariable(variable: DivVariable): void {
        const varInstance = constructVariable(variable);

        if (varInstance) {
            localVariables.set(variable.name, varInstance);
            variables.set(variable.name, varInstance);
        }
    }

    const startVariables = json?.card?.variables;
    if (Array.isArray(startVariables)) {
        startVariables.forEach(variable => {
            if (variable && variable.name) {
                if (localVariables.has(variable.name)) {
                    logError(wrapError(new Error('Duplicate variable'), {
                        additional: {
                            name: variable.name
                        }
                    }));

                    return;
                }

                declVariable(variable);
            }
        });
    }

    $: if (json?.card?.variables && Array.isArray(json.card.variables) && json.card.variables !== startVariables) {
        json.card.variables.forEach(variable => {
            if (variable && variable.name && !localVariables.has(variable.name)) {
                declVariable(variable);
            }
        });
    }

    const palette = json.palette;
    if (palette) {
        const list = palette[currentTheme];
        list.forEach(item => {
            if (localVariables.has(item.name)) {
                logError(wrapError(new Error('Duplicate variable'), {
                    additional: {
                        name: item.name
                    }
                }));

                return;
            }

            try {
                const varInstance = createVariable(item.name, 'color', item.color);

                localVariables.set(item.name, varInstance);
                variables.set(item.name, varInstance);
            } catch (err: any) {
                logError(wrapError(err, {
                    additional: {
                        name: item.name
                    }
                }));
            }
        });
    }

    for (const [varName, variable] of globalVariables) {
        if (!variables.has(varName)) {
            variables.set(varName, variable);
        }
    }

    globalVariablesStore.subscribe(newVarName => {
        if (newVarName && !variables.has(newVarName)) {
            const varInstance = globalVariables.get(newVarName) as Variable;
            variables.set(newVarName, varInstance);

            const awaitingStore = awaitingGlobalVariables.get(newVarName);

            if (awaitingStore) {
                let counter = 0;
                varInstance.subscribe(() => {
                    awaitingStore.set(++counter);
                });
            }
        }
    });

    const initVariableTriggers = () => {
        processVariableTriggers(undefined, json?.card?.variable_triggers);
    };

    const timers = json?.card?.timers;
    if (timers && typeof document !== 'undefined') {
        const controller = timersController = new TimersController({
            logError,
            applyVars: json => getJsonWithVars(logError, json),
            hasVariableWithType,
            setVariableValue,
            execAnyActions
        });
        timers.forEach(timer => controller.createTimer(timer));
    }

    $: states = json?.card?.states;
    const rootComponentContext = produceComponentContext();
    let rootStateComponentContext: ComponentContext | undefined;
    $: if (states && !hasError && !hsaIdError) {
        const rootStateDiv: DivBaseData = {
            type: 'state',
            id: 'root',
            width: {
                type: 'match_parent',
            },
            height: {
                type: 'match_parent',
            },
            states: states.map(state => ({
                state_id: state.state_id.toString(),
                div: state.div
            }))
        } as DivBaseData;

        rootStateComponentContext = rootComponentContext.produceChildContext(rootStateDiv, {
            isRootState: true
        });
    }

    function onTooltipClose(internalId: number): void {
        tooltips = tooltips.filter(it => it.internalId !== internalId);
    }

    /**
     * Fix for the :active pseudo-class on iOS
     */
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    function emptyTouchstartHandler() {}

    onMount(() => {
        rootInstancesCount++;

        if (rootInstancesCount === 1) {
            window.addEventListener('keydown', onWindowKeyDown);
            window.addEventListener('pointerdown', onWindowPointerDown);
        }

        // delay for children components initialization
        tick().then(() => {
            if (isMounted) {
                initVariableTriggers();
            }
        });
    });

    onDestroy(() => {
        isMounted = false;
        rootInstancesCount--;

        if (!rootInstancesCount) {
            window.removeEventListener('keydown', onWindowKeyDown);
            window.removeEventListener('pointerdown', onWindowPointerDown);
        }

        for (const [_id, instance] of animators) {
            instance.stop();
        }

        if (timersController) {
            timersController.destroy();
        }

        tooltips.forEach(info => {
            if (info.timeoutId) {
                clearTimeout(info.timeoutId);
                info.timeoutId = null;
            }
        });

        timeouts.forEach(timeout => {
            clearTimeout(timeout);
        });

        if (!globalVariablesController) {
            cleanControllerStore(variablesController);
        }
    });
</script>

{#if !hasError && !hsaIdError && rootStateComponentContext}
    <div
        class="{css.root}{$isDesktop ? ` ${css.root_platform_desktop}` : ''}{mix ? ` ${mix}` : ''}"
        on:touchstart={emptyTouchstartHandler}
        dir={$directionStore}
    >
        <RootSvgFilters {svgFiltersMap} />

        <Unknown
            componentContext={rootStateComponentContext}
        />

        {#if tooltips}
            {#each tooltips as item (item.internalId)}
                <TooltipView
                    ownerNode={item.ownerNode}
                    data={item.desc}
                    internalId={item.internalId}
                    parentComponentContext={item.componentContext || rootStateComponentContext}
                />
            {/each}
        {/if}

        {#if menu}
            <Menu
                ownerNode={menu.node}
                items={menu.items}
                parentComponentContext={menu.componentContext || rootStateComponentContext}
                on:close={() => menu = undefined}
            />
        {/if}
    </div>
{/if}
