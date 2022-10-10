<script lang="ts">
    import type { Readable, Writable } from 'svelte/types/runtime/store';
    import { setContext } from 'svelte';
    import { derived, writable } from 'svelte/store';

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
        Theme
    } from '../../typings/common';
    import type { AppearanceTransition, DivBaseData, TransitionChange } from '../types/base';
    import type { SwitchElements, Overflow } from '../types/switch-elements';
    import type { DivStateData } from '../types/state';
    import Unknown from './utilities/Unknown.svelte';
    import RootSvgFilters from './utilities/RootSvgFilters.svelte';
    import { ROOT_CTX, RootCtxValue, Running } from '../context/root';
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
    import { createVariable, TYPE_TO_CLASS, Variable, VariableType, VariableValue } from '../expressions/variable';
    import {
        getControllerStore,
        getControllerVars,
        GlobalVariablesController
    } from '../expressions/globalVariablesController';

    export let id: string;
    export let json: Partial<DivJson> = {};
    export let platform: Platform = 'auto';
    export let theme: Theme = 'system';
    export let globalVariablesController: GlobalVariablesController = new GlobalVariablesController();
    export let onError: ErrorCallback | undefined = undefined;
    export let onStat: StatCallback | undefined = undefined;
    export let onCustomAction: CustomActionCallback | undefined = undefined;
    export let onComponent: ComponentCallback | undefined = undefined;

    let isDesktop = platform === 'desktop';
    if (platform === 'auto' && typeof matchMedia !== 'undefined') {
        const touchQuery = matchMedia('(any-pointer: coarse)');
        isDesktop = !touchQuery.matches;
        touchQuery.addListener(() => {
            isDesktop = !touchQuery.matches;
        });
    }

    let currentTheme: 'light' | 'dark' = 'light';
    if (theme === 'light' || theme === 'dark') {
        currentTheme = theme;
    } else if (theme === 'system') {
        if (typeof matchMedia !== 'undefined') {
            const themeQuery = matchMedia('(prefers-color-scheme: dark)');
            currentTheme = themeQuery.matches ? 'dark' : 'light';
            themeQuery.addListener(() => {
                currentTheme = themeQuery.matches ? 'dark' : 'light';

                updateTheme();
            });
        } else {
            currentTheme = 'light';
        }
    } else {
        logError(wrapError(new Error('Unsupported theme')));
    }

    let hasError = false;

    if (!json) {
        hasError = true;
        logError(wrapError(new Error('"json" prop is required')));
    }

    if (!id) {
        hasError = true;
        logError(wrapError(new Error('"id" prop is required')));
    }

    let initialError = simpleCheckInput(json);
    let templateContext: TemplateContext = {};
    const templates = json.templates || {};

    if (initialError) {
        hasError = true;
        logError(initialError);
    }

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

    /** @deprecated */
    export function setVariable(name: string, value: VariableValue): void {
        if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
            const varInstance = variables.get(name);
            if (varInstance) {
                varInstance.setValue(value);
            }
        }
    }

    /** @deprecated */
    export function getVariable(name: string): VariableValue | undefined {
        const varInstance = variables.get(name);

        return varInstance?.getValue();
    }

    function getVariableInstance(name: string, type: VariableType): Variable | undefined {
        const variable = variables.get(name);

        if (variable) {
            const foundType = variable.getType();

            if (foundType !== type) {
                logError(wrapError(new Error(`Variable should have type "${type}"`), {
                    additional: {
                        name,
                        foundType
                    }
                }));
                return;
            }
        }

        return variable;
    }

    function getDerivedFromVars<T>(jsonProp: T): Readable<MaybeMissing<T>> {
        if (!jsonProp) {
            return constStore(jsonProp);
        }

        const prepared = prepareVars(jsonProp, logError);
        if (!prepared.vars.length) {
            return constStore(jsonProp);
        }
        const stores = prepared.vars.map(name => {
            return variables.get(name) || awaitVariableChanges(name);
        }).filter(Truthy);

        return derived(stores, () => prepared.applyVars(variables));
    }

    function getJsonWithVars<T>(jsonProp: T): MaybeMissing<T> {
        const prepared = prepareVars(jsonProp, logError);

        if (!prepared.vars.length) {
            return jsonProp;
        }

        return prepared.applyVars(variables);
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

    function processTemplate(json: DivBaseData, templateContext: TemplateContext): {
        json: DivBaseData;
        templateContext: TemplateContext;
    } {
        if (!json) {
            return {
                json,
                templateContext
            };
        }

        const usedTypes = new Set([json.type]);

        while (json.type in templates) {
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

    function registerComponentReal({
        node,
        json,
        origJson,
        templateContext
    }: {
        node: HTMLElement;
        json: Partial<DivBaseData>;
        origJson: DivBase | undefined;
        templateContext: TemplateContext;
    }): void {
        if (onComponent) {
            onComponent({
                type: 'mount',
                node,
                json: json as DivBase,
                origJson,
                templateContext
            });
        }
    }
    function unregisterComponentReal({
        node
    }: {
        node: HTMLElement;
    }): void {
        if (onComponent) {
            onComponent({
                type: 'destroy',
                node
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

    function addSvgFilter(color: string): string {
        svgFilterUsages[color] = svgFilterUsages[color] || 0;
        ++svgFilterUsages[color];

        if (svgFiltersMap[color]) {
            return svgFiltersMap[color];
        }

        const filterId = `${genId('root')}-svg-filter`;
        svgFiltersMap = {
            ...svgFiltersMap,
            [color]: filterId
        };

        return filterId;
    }

    function removeSvgFilter(color: string | undefined): void {
        if (!color || !svgFilterUsages[color]) {
            return;
        }

        if (--svgFilterUsages[color] === 0) {
            svgFiltersMap = Object.keys(svgFiltersMap).reduce((acc, item) => {
                if (svgFilterUsages[item]) {
                    acc[item] = svgFiltersMap[item];
                }
                return acc;
            }, {} as typeof svgFiltersMap);
        }
    }

    function setState(stateId: string | null): void {
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

    export function execAction(action: Action | VisibilityAction): void {
        const actionUrl = String(action.url);

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
                                variableInstance.set(value);
                            } else {
                                logError(wrapError(new Error('Cannot find variable'), {
                                    additional: {
                                        name
                                    }
                                }));
                            }
                        } else {
                            logError(wrapError(new Error('Incorrect set_variable_action'), {
                                additional: {
                                    url
                                }
                            }));
                        }
                        break;
                    default:
                        logError(wrapError(new Error('Unknown type of action'), {
                            additional: {
                                url: actionUrl
                            }
                        }));
                }
            } catch (err: any) {
                logError(wrapError(err, {
                    additional: {
                        url: actionUrl
                    }
                }));
            }
        }
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

    function filterDefinedVars(vars: Set<string>): Set<string> {
        for (const varName of vars) {
            if (variables.has(varName)) {
                vars.delete(varName);
            }
        }

        return vars;
    }

    function awaitVariableChanges(variableName: string): Readable<any> {
        const store = awaitingGlobalVariables.get(variableName) || writable(undefined);

        if (!awaitingGlobalVariables.has(variableName)) {
            awaitingGlobalVariables.set(variableName, store);
        }

        return store;
    }

    function waitForVars(vars: string[]): Promise<void> {
        const remaining = new Set(vars);

        filterDefinedVars(remaining);

        if (!remaining.size) {
            return Promise.resolve();
        }

        return new Promise(resolve => {
            const unsubscribe = globalVariablesStore.subscribe(() => {
                filterDefinedVars(remaining);

                if (!remaining.size) {
                    unsubscribe();
                    resolve();
                }
            });
        });
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

    setContext<RootCtxValue>(ROOT_CTX, {
        logError,
        logStat,
        hasTemplate,
        processTemplate,
        genId,
        genClass,
        execAction,
        execCustomAction,
        isRunning,
        setRunning,
        registerInstance,
        unregisterInstance,
        addSvgFilter,
        removeSvgFilter,
        getDerivedFromVars,
        getJsonWithVars,
        getStore,
        getVariable: getVariableInstance,
        registerComponent: process.env.DEVTOOL ? registerComponentReal : undefined,
        unregisterComponent: process.env.DEVTOOL ? unregisterComponentReal : undefined
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
            _templateContext: TemplateContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement,
            _direction: 'in' | 'out'
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionIn(
            _json: DivBaseData,
            _templateContext: TemplateContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionOut(
            _json: DivBaseData,
            _templateContext: TemplateContext,
            _transitions: AppearanceTransition,
            _node: HTMLElement
        ) {
            return Promise.resolve();
        },
        registerChildWithTransitionChange(
            _json: DivBaseData,
            _templateContext: TemplateContext,
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

    const startVariables = json?.card?.variables;
    if (Array.isArray(startVariables)) {
        startVariables.forEach(variable => {
            if (variable && variable.type in TYPE_TO_CLASS && variable.name) {
                if (localVariables.has(variable.name)) {
                    logError(wrapError(new Error('Duplicate variable'), {
                        additional: {
                            name: variable.name
                        }
                    }));

                    return;
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

    const variableTriggers = json?.card?.variable_triggers;
    if (Array.isArray(variableTriggers)) {
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

                waitForVars(exprVars).then(() => {
                    const stores = exprVars.map(name => variables.get(name)).filter(Truthy);

                    derived(stores, () => {
                        return evalExpression(variables, ast);
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
                                    execAction(resultAction as Action);
                                }
                            });
                        }

                        prevConditionResult = Boolean(conditionResult.value);
                    });
                });
            } catch (err) {
                logError(wrapError(new Error('Unable to parse variable_trigger'), {
                    additional: {
                        condition: trigger.condition
                    }
                }));
            }
        });
    }

    const states = json.card?.states;
    const rootStateDiv: DivStateData | undefined = (states && !hasError) ? {
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
    } : undefined;

    /**
     * Fix for the :active pseudo-class on iOS
     */
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    function emptyTouchstartHandler() {}
</script>

{#if !hasError && rootStateDiv}
    <div
        class="{css.root}{isDesktop ? ` ${css.root_platform_desktop}` : ''}"
        on:touchstart={emptyTouchstartHandler}
    >
        <RootSvgFilters {svgFiltersMap} />
        <Unknown div={rootStateDiv} {templateContext} />
    </div>
{/if}
