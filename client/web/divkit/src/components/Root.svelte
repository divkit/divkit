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
        Direction
    } from '../../typings/common';
    import type { CustomComponentDescription } from '../../typings/custom';
    import type { AppearanceTransition, DivBaseData, Tooltip, TransitionChange } from '../types/base';
    import type { SwitchElements, Overflow } from '../types/switch-elements';
    import type { TintMode } from '../types/image';
    import type { VideoElements } from '../types/video';
    import type { Patch } from '../types/patch';
    import type { ComponentContext } from '../types/componentContext';
    import Unknown from './utilities/Unknown.svelte';
    import RootSvgFilters from './utilities/RootSvgFilters.svelte';
    import { FocusableMethods, ParentMethods, ROOT_CTX, RootCtxValue, Running } from '../context/root';
    import { applyTemplate } from '../utils/applyTemplate';
    import { wrapError, WrappedError } from '../utils/wrapError';
    import { simpleCheckInput } from '../utils/simpleCheckInput';
    import { ACTION_CTX, ActionCtxValue } from '../context/action';
    import { STATE_CTX, StateCtxValue, StateInterface } from '../context/state';
    import { constStore } from '../utils/constStore';
    import {
        MaybeMissing,
        prepareVars
    } from '../expressions/json';
    import { storesMap } from '../stores';
    import { evalExpression } from '../expressions/eval';
    import { parse } from '../expressions/expressions';
    import { gatherVarsFromAst } from '../expressions/utils';
    import { Truthy } from '../utils/truthy';
    import { createConstVariable, createVariable, TYPE_TO_CLASS, Variable, VariableType } from '../expressions/variable';
    import {
        getControllerStore,
        getControllerVars,
        GlobalVariablesController
    } from '../expressions/globalVariablesController';
    import { getUrlSchema, isBuiltinSchema } from '../utils/url';
    import { TimersController } from '../utils/timers';
    import { arrayInsert, arrayRemove } from '../actions/array';
    import { copyToClipboard } from '../actions/copyToClipboard';
    import { filterEnabledActions } from '../utils/filterEnabledActions';

    export let id: string;
    export let json: Partial<DivJson> = {};
    export let platform: Platform = 'auto';
    export let theme: Theme = 'system';
    export let globalVariablesController: GlobalVariablesController = new GlobalVariablesController();
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

    // Will notify about new global variables
    const globalVariablesStore = getControllerStore(globalVariablesController);
    // Global variables only
    const globalVariables = getControllerVars(globalVariablesController);
    // Local variables only
    const localVariables = new Map<string, Variable>();
    // Local and global variables combined, with local in precedence
    const variables = new Map<string, Variable>();
    // Stores for notify unset global variables
    const awaitingGlobalVariables = new Map<string, Writable<any>>();
    let timersController: TimersController | null = null;

    let tooltipCounter = 0;
    let tooltips: {
        internalId: number;
        ownerNode: HTMLElement;
        desc: MaybeMissing<Tooltip>;
        timeoutId: number | null;
    }[] = [];

    function mergeVars(
        variables0: Map<string, Variable>,
        variables1: Map<string, Variable> | undefined
    ): Map<string, Variable>;
    function mergeVars(
        variables0: Map<string, Variable> | undefined,
        variables1: Map<string, Variable> | undefined
    ): Map<string, Variable> | undefined;
    function mergeVars(
        variables0: Map<string, Variable> | undefined,
        variables1: Map<string, Variable> | undefined
    ): Map<string, Variable> | undefined {
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
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex = false
    ): Readable<MaybeMissing<T>> {
        if (!jsonProp) {
            return constStore(jsonProp);
        }

        const vars = mergeVars(variables, additionalVars);

        const prepared = prepareVars(jsonProp, logError);
        if (!prepared.vars.length) {
            if (prepared.hasExpression) {
                return constStore(prepared.applyVars(vars));
            }
            return constStore(jsonProp);
        }
        const stores = prepared.vars.map(name => {
            return vars.get(name) || awaitVariableChanges(name);
        }).filter(Truthy);

        return derived(stores, () => prepared.applyVars(vars, keepComplex));
    }

    function getJsonWithVars<T>(
        jsonProp: T,
        additionalVars?: Map<string, Variable>,
        keepComplex = false
    ): MaybeMissing<T> {
        const prepared = prepareVars(jsonProp, logError);

        if (!prepared.hasExpression) {
            return jsonProp;
        }

        const vars = mergeVars(variables, additionalVars);

        return prepared.applyVars(vars, keepComplex);
    }

    function preparePrototypeVariables(name: string, data: Record<string, unknown>): Map<string, Variable> {
        const map = new Map<string, Variable>();

        const dict = createConstVariable(name, 'dict', data);
        map.set(name, dict);

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
        node: HTMLElement;
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

    function setState(stateId: string | null): void {
        if (!process.env.ENABLE_COMPONENT_STATE && process.env.ENABLE_COMPONENT_STATE !== undefined) {
            throw new Error('State is not supported');
        }

        if (!stateId) {
            throw new Error('Missing state id');
        }

        let state: StateInterface = stateInterface;
        let parts = stateId.split('/');

        parts = ['root', ...parts];
        if (parts.length < 2 || parts.length % 2 !== 0) {
            throw new Error('Incorrect state id format');
        }

        for (let i = 0; i < parts.length; i += 2) {
            const divId = parts[i];
            const selectedStateId = parts[i + 1];

            let childState = state.getChild(divId);
            if (childState) {
                childState.setState(selectedStateId);
                state = childState;
            } else {
                return;
            }
        }
    }

    function setCurrentItem(id: string | null, item: string | null): void {
        if (!id || !item) {
            throw new Error('Missing data for "set-current-item" action');
        }

        if (isNaN(Number(item))) {
            throw new Error('Incorrect item for "set-current-item" action');
        }

        const instance = getInstance<SwitchElements>(id);

        if (instance) {
            instance.setCurrentItem(Number(item));
        }
    }

    function setPreviousItem(id: string | null, overflow: string | null): void {
        if (!id) {
            throw new Error('Missing id for "set-previous-item" action');
        }

        if (overflow && overflow !== 'clamp' && overflow !== 'ring') {
            throw new Error('Incorrect overflow value for "set-previous-item" action');
        }

        overflow = overflow || 'clamp';

        const instance = getInstance<SwitchElements>(id);

        if (instance) {
            instance.setPreviousItem(overflow as Overflow);
        }
    }

    function setNextItem(id: string | null, overflow: string | null): void {
        if (!id) {
            throw new Error('Missing id for "set-next-item" action');
        }

        if (overflow && overflow !== 'clamp' && overflow !== 'ring') {
            throw new Error('Incorrect overflow value for "set-next-item" action');
        }

        overflow = overflow || 'clamp';

        const instance = getInstance<SwitchElements>(id);

        if (instance) {
            instance.setNextItem(overflow as Overflow);
        }
    }

    function callVideoAction(
        id: string | null,
        action: string | null,
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
        url: string | null,
        action: MaybeMissing<Action | VisibilityAction | DisappearAction>,
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
                            execAnyActions(action.download_callbacks?.on_fail_actions);
                            return;
                        }
                    }
                    json.patch.changes.forEach(change => {
                        const methods = parentOfMap.get(change.id);
                        if (methods) {
                            methods.replaceWith(change.id, change.items);
                        }
                    });
                    execAnyActions(action.download_callbacks?.on_success_actions);
                }
            }).catch(err => {
                log(wrapError(new Error('Failed to download the patch'), {
                    additional: {
                        url,
                        originalError: err
                    }
                }));
                execAnyActions(action.download_callbacks?.on_fail_actions);
            });
        } else {
            log(wrapError(new Error('Missing url in download action'), {
                additional: {
                    url
                }
            }));
        }
    }

    function callShowTooltip(id: string | null, multiple: string | null, componentContext?: ComponentContext): void {
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
        if (multiple !== 'true' && tooltips.some(it => it.desc.id === id)) {
            return;
        }

        const info = {
            internalId: ++tooltipCounter,
            ownerNode: item.onwerNode,
            desc: item.tooltip,
            timeoutId: 0
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

    function callHideTooltip(id: string | null, componentContext?: ComponentContext): void {
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

    export function execAction(action: MaybeMissing<Action | VisibilityAction | DisappearAction>): void {
        execActionInternal(getJsonWithVars(action));
    }

    function execActionInternal(
        action: MaybeMissing<Action | VisibilityAction | DisappearAction>,
        componentContext?: ComponentContext
    ): void {
        const actionUrl = action.url ? String(action.url) : '';
        const actionTyped = action.typed;

        if (!filterEnabledActions(action)) {
            return;
        }

        const log = (componentContext?.logError || logError);

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
                        setState(params.get('state_id'));
                        break;
                    case 'set_current_item':
                        setCurrentItem(params.get('id'), params.get('item'));
                        break;
                    case 'set_previous_item':
                        setPreviousItem(params.get('id'), params.get('overflow'));
                        break;
                    case 'set_next_item':
                        setNextItem(params.get('id'), params.get('overflow'));
                        break;
                    case 'set_variable':
                        const name = params.get('name');
                        const value = params.get('value');

                        if (name && value !== null) {
                            const variableInstance = variables.get(name);
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
                        callDownloadAction(params.get('url'), action, componentContext);
                        break;
                    case 'show_tooltip':
                        callShowTooltip(params.get('id'), params.get('multiple'), componentContext);
                        break;
                    case 'hide_tooltip':
                        callHideTooltip(params.get('id'), componentContext);
                        break;
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
                        const variableInstance = variables.get(name);
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
                    arrayInsert(variables, log, actionTyped);
                    break;
                case 'array_remove_value':
                    arrayRemove(variables, log, actionTyped);
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
                        execActionInternal(action, opts.componentContext);
                        await tick();
                    } else if (action.log_id) {
                        execCustomAction(action as Action & { url: string });
                        await tick();
                    }
                }
            } else if (actionTyped) {
                execActionInternal(action, opts.componentContext);
            }
        }
        filtered.forEach(action => {
            if (action.log_id) {
                logStat('click', action as Action);
            }
        });
    }

    function execCustomAction(action: (Action | VisibilityAction) & { url: string }): void {
        onCustomAction?.(action);
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

    function getExtensionContext(): DivExtensionContext {
        return {
            variables,
            logError
        };
    }

    function produceComponentContext(from?: ComponentContext | undefined): ComponentContext {
        const res: ComponentContext = {
            json: {} as DivBaseData,
            path: [],
            templateContext: {},
            logError(error) {
                error.additional = error.additional || {};
                error.additional.path = res.path.join('/');
                if (process.env.DEVTOOL) {
                    error.additional.json = res.json;
                    error.additional.origJson = res.origJson;
                }
                logError(error);
            },
            execAnyActions(actions, opts = {}) {
                return execAnyActions(actions, {
                    componentContext: res,
                    processUrls: opts.processUrls
                });
            },
            getDerivedFromVars(jsonProp, additionalVars, keepComplex = false) {
                return getDerivedFromVars(jsonProp, mergeVars(res.variables, additionalVars), keepComplex);
            },
            getJsonWithVars(jsonProp, additionalVars, keepComplex = false) {
                return getJsonWithVars(jsonProp, mergeVars(res.variables, additionalVars), keepComplex);
            },
            produceChildContext(div, opts = {}) {
                const componentContext = produceComponentContext(res);

                let childJson: MaybeMissing<DivBaseData> = div;
                let childContext: TemplateContext = res.templateContext;

                const {
                    templateContext: childProcessedContext,
                    json: childProcessedJson
                } = processTemplate(childJson, childContext);

                componentContext.json = childProcessedJson;
                componentContext.templateContext = childProcessedContext;
                componentContext.origJson = div;

                if (opts.path !== undefined/*  && !res.isRootState */) {
                    componentContext.path.push(String(opts.path));
                }
                if (div.type && !opts.isRootState) {
                    componentContext.path.push(div.type);
                }

                componentContext.variables = mergeVars(res.variables, opts.variables);

                if (opts.tooltips) {
                    componentContext.tooltips = opts.tooltips;
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
                const variable = (res.variables || variables).get(varName);

                if (variable) {
                    const foundType = variable.getType();

                    if (foundType !== type) {
                        res.logError(wrapError(new Error(`Variable should have type "${type}"`), {
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
        };

        if (from) {
            res.path = from.path.slice();

            if (from.fakeElement) {
                res.fakeElement = true;
            }
        } else {
            res.json = {
                type: 'root'
            };
            res.isRootState = true;
        }

        return res;
    }

    setContext<RootCtxValue>(ROOT_CTX, {
        logStat,
        hasTemplate,
        processTemplate,
        genId,
        genClass,
        execAction: execActionInternal,
        execCustomAction,
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
        preparePrototypeVariables,
        getStore,
        getCustomization,
        getBuiltinProtocols,
        getExtension,
        getExtensionContext,
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

    const stateInterface: StateInterface = {
        setState(_stateId: string): void {
            throw new Error('Not implemented');
        },
        getChild(id: string): StateInterface | undefined {
            if (childStateMap && childStateMap.has(id)) {
                return childStateMap.get(id);
            }

            logError(wrapError(new Error('Missing state block with id'), {
                additional: {
                    id
                }
            }));

            return undefined;
        }
    };

    let childStateMap: Map<string, StateInterface> | null = null;
    setContext<StateCtxValue>(STATE_CTX, {
        registerInstance(id: string, block: StateInterface) {
            if (!childStateMap) {
                childStateMap = new Map();
            }

            if (childStateMap.has(id)) {
                logError(wrapError(new Error('Duplicate state with id'), {
                    additional: {
                        id
                    }
                }));
            } else {
                childStateMap.set(id, block);
            }
        },
        unregisterInstance(id: string) {
            childStateMap?.delete(id);
        },
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

    function declVariable(variable: DivVariable): void {
        if (!(variable.type in TYPE_TO_CLASS)) {
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
            const varInstance = createVariable(variable.name, variable.type, variable.value);

            localVariables.set(variable.name, varInstance);
            variables.set(variable.name, varInstance);
        } catch (err: any) {
            logError(wrapError(err, {
                additional: {
                    name: variable.name
                }
            }));
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
        const variableTriggers = json?.card?.variable_triggers;
        if (Array.isArray(variableTriggers)) {
            if (process.env.ENABLE_EXPRESSIONS) {
                variableTriggers.forEach(trigger => {
                    let prevConditionResult = false;

                    if (typeof trigger.condition !== 'string') {
                        logError(wrapError(new Error('variable_trigger has a condition that is not a string'), {
                            additional: {
                                condition: trigger.condition
                            }
                        }));
                        return;
                    }

                    const mode = trigger.mode || 'on_condition';

                    if (mode !== 'on_variable' && mode !== 'on_condition') {
                        logError(wrapError(new Error('variable_trigger has an unsupported mode'), {
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
                            logError(wrapError(new Error('variable_trigger must have variables in the condition'), {
                                additional: {
                                    condition: trigger.condition
                                }
                            }));
                            return;
                        }

                        const stores = exprVars.map(name => variables.get(name) || awaitVariableChanges(name));

                        derived(stores, () => {
                            const res = evalExpression(variables, ast);

                            res.warnings.forEach(logError);

                            return res.result;
                        }).subscribe(conditionResult => {
                            if (conditionResult.type === 'error') {
                                logError(wrapError(new Error('variable_trigger condition execution error'), {
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
                                trigger.actions.forEach(action => {
                                    const resultAction = getJsonWithVars(action);
                                    if (resultAction.log_id) {
                                        execActionInternal(resultAction as Action);
                                    }
                                });
                            }

                            prevConditionResult = Boolean(conditionResult.value);
                        });
                    } catch (err) {
                        logError(wrapError(new Error('Unable to parse variable_trigger'), {
                            additional: {
                                condition: trigger.condition
                            }
                        }));
                    }
                });
            } else {
                logError(wrapError(new Error('variable_trigger is not supported')));
            }
        }
    };

    const timers = json?.card?.timers;
    if (timers && typeof document !== 'undefined') {
        const controller = timersController = new TimersController({
            logError,
            applyVars: getJsonWithVars,
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
        const rootStateDiv = (
            process.env.ENABLE_COMPONENT_STATE ||
            process.env.ENABLE_COMPONENT_STATE === undefined
        ) ?
            {
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
            } :
            states[0].div;

        rootStateComponentContext = rootComponentContext.produceChildContext(rootStateDiv, {
            tooltips,
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

        initVariableTriggers();
    });

    onDestroy(() => {
        rootInstancesCount--;

        if (!rootInstancesCount) {
            window.removeEventListener('keydown', onWindowKeyDown);
            window.removeEventListener('pointerdown', onWindowPointerDown);
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
    </div>
{/if}
