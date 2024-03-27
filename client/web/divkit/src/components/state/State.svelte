<script lang="ts">
    import { setContext, getContext, tick, onDestroy } from 'svelte';

    import css from './State.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivStateData, State } from '../../types/state';
    import type { AnyTransition, AppearanceTransition, DivBaseData, TransitionChange } from '../../types/base';
    import type { ChangeBoundsTransition } from '../../types/base';
    import type { ComponentContext } from '../../types/componentContext';
    import type { MaybeMissing } from '../../expressions/json';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { wrapError } from '../../utils/wrapError';
    import { STATE_CTX, StateCtxValue, StateInterface } from '../../context/state';
    import { calcMaxDuration, inOutTransition } from '../../utils/inOutTransition';
    import { changeBoundsTransition } from '../../utils/changeBoundsTransition';
    import { flattenTransition } from '../../utils/flattenTransition';
    import Outer from '../utilities/Outer.svelte';
    import Unknown from '../utilities/Unknown.svelte';
    import TooltipView from '../tooltip/Tooltip.svelte';

    export let componentContext: ComponentContext<DivStateData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    const stateCtx = getContext<StateCtxValue>(STATE_CTX);

    let hasError = false;

    let animationRoot: HTMLElement | undefined;
    let transitionChangeBoxes: Map<string, DOMRect> = new Map();
    let childrenIds = new Set<string>();

    let childStateMap: Map<string, StateInterface> | null = null;
    let animationList: (AnimationItemWithMaxDuration | ChangeBoundsItem)[] = [];
    let childrenWithTransitionIn: ChildWithTransition[] = [];
    let childrenWithTransitionOut: ChildWithTransition[] = [];
    let childrenWithTransitionChange: ChildWithTransitionChange[] = [];

    let prevStateId: string | undefined;
    $: stateId = componentContext.json.div_id || componentContext.json.id;
    let selectedId: string | undefined;
    let selectedComponentContext: ComponentContext | undefined;
    $: jsonDefaultStateId = componentContext.getJsonWithVars(componentContext.json.default_state_id);
    $: stateVariableName = componentContext.json.state_id_variable;
    $: stateVariable = stateVariableName ?
        componentContext.getVariable(stateVariableName, 'string') :
        null;
    let inited = false;

    $: origJson = componentContext.origJson;

    function rebind(): void {
        inited = false;
    }

    $: if (origJson) {
        rebind();
    }

    $: if (stateId) {
        hasError = false;
    } else {
        hasError = true;
        componentContext.logError(wrapError(new Error('Missing "id" prop for div "state"')));
    }

    $: if (componentContext.json) {
        childrenIds = new Set<string>();
    }

    $: items = Array.isArray(componentContext.json.states) && componentContext.json.states || [];
    $: parentOfItems = items.map(it => {
        return it.div;
    });

    $: {
        if (!items?.length) {
            hasError = true;
            componentContext.logError(wrapError(new Error('Empty "states" prop for div "state"')));
        } else {
            hasError = false;
        }
    }

    function selectState(selectedState: MaybeMissing<State> | null): void {
        selectedComponentContext = selectedState?.div ? componentContext.produceChildContext(selectedState.div, {
            path: selectedState.state_id || '<unknown>'
        }) : undefined;
    }

    function replaceItems(newItems: (MaybeMissing<DivBaseData> | undefined)[]): void {
        const states = componentContext.json.states;

        if (!states) {
            return;
        }

        items = states.map((it, index) => {
            return {
                ...it,
                div: newItems[index]
            };
        });

        componentContext.json = {
            ...componentContext.json,
            states: items
        };
        if (selectedId) {
            selectState(items.find(it => it.state_id === selectedId) || null);
        }
    }

    interface AnimationItem {
        json: DivBaseData;
        componentContextCopy: ComponentContext;
        elementBbox: DOMRect;
        rootBbox: DOMRect;
        transitions: AnyTransition[];
        alpha?: number;
        width: number;
        height: number;
        offsetTop: number;
        offsetLeft: number;
        direction: 'in' | 'out';
        resolvePromise?: (val?: void) => void;
        node: HTMLElement;
    }
    interface AnimationItemWithMaxDuration extends AnimationItem {
        maxDuration: number;
    }
    interface ChangeBoundsItem {
        json: DivBaseData;
        componentContextCopy: ComponentContext;
        rootBbox: DOMRect;
        beforeBbox: DOMRect;
        afterBbox: DOMRect;
        transition: ChangeBoundsTransition;
        resolvePromise?: (val?: void) => void;
        node: HTMLElement;
    }

    interface ChildWithTransition {
        json: DivBaseData;
        parentComponentContext: ComponentContext;
        transitions: AppearanceTransition;
        node: HTMLElement;
        resolvePromise?: (val?: void) => void;
    }
    interface ChildWithTransitionChange {
        id: string;
        json: DivBaseData;
        parentComponentContext: ComponentContext;
        transitions: TransitionChange;
        node: HTMLElement;
        resolvePromise?: (val?: void) => void;
    }

    function haveFadeTransition(list: AnyTransition[]): boolean {
        return list.some(it => it.type === 'fade');
    }

    function getItemAnimation(rootBbox: DOMRect, child: ChildWithTransition, direction: 'in' | 'out'): AnimationItem {
        let { json, parentComponentContext, transitions, node } = child;
        json = componentContext.getJsonWithVars(json) as DivBaseData;
        transitions = componentContext.getJsonWithVars(transitions) as AppearanceTransition;

        const transitionsList: AnyTransition[] = flattenTransition(transitions);
        const bbox = node.getBoundingClientRect();
        const jsonCopy = {
            ...json,
            margins: undefined,
            alpha: haveFadeTransition(transitionsList) ? undefined : json.alpha
        };

        return {
            json: jsonCopy,
            componentContextCopy: parentComponentContext.produceChildContext(jsonCopy, {
                fake: true
            }),
            elementBbox: bbox,
            rootBbox,
            transitions: transitionsList,
            alpha: json.alpha,
            width: bbox.width,
            height: bbox.height,
            offsetTop: bbox.top - rootBbox.top,
            offsetLeft: bbox.left - rootBbox.left,
            direction,
            resolvePromise: child.resolvePromise,
            node: child.node
        };
    }

    function getTransitionChange(transitionOrSet: TransitionChange): ChangeBoundsTransition | null {
        if (transitionOrSet.type === 'change_bounds') {
            return transitionOrSet;
        } else if (transitionOrSet.type === 'set') {
            return getTransitionChange(transitionOrSet.items[0]);
        }
        return null;
    }

    async function setState(stateId: string) {
        if (selectedId === stateId) {
            return;
        }

        rootCtx.setRunning('stateChange', true);

        const wasIds = new Set(childrenIds);

        animationList.forEach(it => {
            if (it.resolvePromise) {
                it.resolvePromise();
            }
        });
        animationList = [];
        let transitionsOutToRun: AnimationItem[] = [];
        if (animationRoot) {
            const rootBbox = animationRoot.getBoundingClientRect();
            transitionsOutToRun = childrenWithTransitionOut
                .map(it => getItemAnimation(rootBbox, it, 'out'));
        }
        childrenWithTransitionChange.forEach(child => {
            transitionChangeBoxes.set(child.id, child.node.getBoundingClientRect());
        });
        childrenWithTransitionIn = [];
        childrenWithTransitionOut = [];
        childrenWithTransitionChange = [];

        const newState = items.find(it => it.state_id === stateId) || null;
        if (newState) {
            selectedId = stateId;
            stateVariable?.setValue(selectedId);
            selectState(newState);
        } else {
            componentContext.logError(wrapError(new Error('Cannot find state with id'), {
                additional: {
                    stateId
                }
            }));
        }

        await tick();

        if (!animationRoot) {
            return;
        }
        const rootBbox = animationRoot.getBoundingClientRect();

        let transitionsInToRun: AnimationItem[] =
            childrenWithTransitionIn.filter(it => {
                if (it.json.id && !wasIds.has(it.json.id)) {
                    return true;
                }
                it.resolvePromise?.();
                return false;
            })
                .map(it => getItemAnimation(rootBbox, it, 'in'));

        transitionsOutToRun = transitionsOutToRun.filter(it => {
            if (it.json.id && !childrenIds.has(it.json.id)) {
                return true;
            }
            it.resolvePromise?.();
            return false;
        });

        const inOutList: AnimationItem[] = transitionsOutToRun.concat(transitionsInToRun);
        const maxDuration = inOutList.reduce((acc: number, item: AnimationItem) => {
            return Math.max(
                acc,
                calcMaxDuration(item.transitions)
            );
        }, 0);

        const changeList: ChangeBoundsItem[] = childrenWithTransitionChange
            .filter(child => transitionChangeBoxes.has(child.id))
            .map(child => {
                const jsonCopy: DivBaseData = {
                    ...child.json,
                    margins: undefined,
                    width: { type: 'match_parent' },
                    height: { type: 'match_parent' },
                };

                const res: ChangeBoundsItem = {
                    json: jsonCopy,
                    componentContextCopy: child.parentComponentContext.produceChildContext(jsonCopy, {
                        fake: true
                    }),
                    rootBbox,
                    beforeBbox: transitionChangeBoxes.get(child.id) as DOMRect,
                    afterBbox: child.node.getBoundingClientRect(),
                    node: child.node,
                    transition: componentContext.getJsonWithVars(
                        getTransitionChange(child.transitions)
                    ) as ChangeBoundsTransition,
                    resolvePromise: child.resolvePromise
                };

                return res;
            });

        animationList = [
            ...inOutList.map(it => {
                return {
                    ...it,
                    maxDuration
                };
            }),
            ...changeList
        ];

        transitionChangeBoxes.clear();

        rootCtx.setRunning('stateChange', false);
    }

    function getChild(id: string): StateInterface | undefined {
        if (childStateMap && childStateMap.has(id)) {
            return childStateMap.get(id);
        }

        componentContext.logError(wrapError(new Error('Missing state block with id'), {
            additional: {
                id
            }
        }));
        return undefined;
    }

    $: if (componentContext.json) {
        if (prevStateId) {
            stateCtx.unregisterInstance(prevStateId);
            prevStateId = undefined;
        }

        if (stateId && !componentContext?.fakeElement) {
            prevStateId = stateId;
            stateCtx.registerInstance(stateId, {
                setState,
                getChild
            });
        }
    }

    setContext<StateCtxValue>(STATE_CTX, {
        registerInstance(id: string, block: StateInterface) {
            if (!childStateMap) {
                childStateMap = new Map();
            }

            if (childStateMap.has(id)) {
                componentContext.logError(wrapError(new Error('Duplicate state with id'), {
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
            json: DivBaseData,
            parentComponentContext: ComponentContext,
            transitions: AppearanceTransition,
            node: HTMLElement,
            direction: 'in' | 'out'
        ) {
            if (!animationRoot) {
                return Promise.resolve();
            }

            const rootBbox = animationRoot.getBoundingClientRect();
            const item: AnimationItem = getItemAnimation(
                rootBbox,
                {
                    json,
                    parentComponentContext,
                    transitions,
                    node
                },
                direction
            );

            const maxDuration = calcMaxDuration(item.transitions);
            const itemWithMaxDuration: AnimationItemWithMaxDuration = {
                ...item,
                maxDuration
            };
            animationList = [
                ...animationList.filter(it => it.node !== item.node),
                itemWithMaxDuration
            ];

            return new Promise<void>(resolve => {
                itemWithMaxDuration.resolvePromise = resolve;
            });
        },
        registerChildWithTransitionIn(
            json: DivBaseData,
            parentComponentContext: ComponentContext,
            transitions: AppearanceTransition,
            node: HTMLElement
        ) {
            const item: ChildWithTransition = {
                json,
                parentComponentContext,
                transitions,
                node
            };
            childrenWithTransitionIn.push(item);

            return new Promise<void>(resolve => {
                item.resolvePromise = resolve;
            });
        },
        registerChildWithTransitionOut(
            json: DivBaseData,
            parentComponentContext: ComponentContext,
            transitions: AppearanceTransition,
            node: HTMLElement
        ) {
            const item: ChildWithTransition = {
                json,
                parentComponentContext,
                transitions,
                node
            };
            childrenWithTransitionOut.push(item);

            return new Promise<void>(resolve => {
                item.resolvePromise = resolve;
            });
        },
        registerChildWithTransitionChange(
            json: DivBaseData,
            parentComponentContext: ComponentContext,
            transitions: TransitionChange,
            node: HTMLElement
        ) {
            const id = json.id;

            if (!id) {
                return Promise.resolve();
            }

            const item: ChildWithTransitionChange = {
                id,
                json,
                parentComponentContext,
                transitions,
                node
            };
            childrenWithTransitionChange.push(item);

            return new Promise<void>(resolve => {
                item.resolvePromise = resolve;
            });
        },
        hasTransitionChange(id?: string) {
            if (!id) {
                return false;
            }

            return transitionChangeBoxes.has(id);
        },
        registerChild(id: string): void {
            childrenIds.add(id);
        },
        unregisterChild(id: string): void {
            childrenIds.delete(id);
        }
    });

    function initDefaultState(items: MaybeMissing<State>[]): void {
        if (inited) {
            return;
        }
        inited = true;

        if (items.length) {
            const defaultVal = stateVariable?.getValue() || jsonDefaultStateId;
            if (defaultVal) {
                selectedId = defaultVal;
                const selectedState = items.find(it => it.state_id === selectedId) || null;
                selectState(selectedState);
                if (!selectedState) {
                    componentContext.logError(wrapError(new Error('Cannot find state for default_state_id'), {
                        additional: {
                            selectedId
                        }
                    }));
                }
            } else {
                const selectedState = items[0];
                selectedId = selectedState.state_id;
                selectState(selectedState);
            }

            if (stateVariable) {
                stateVariable.setValue(selectedId);
                stateVariable.subscribe(val => {
                    setState(val);
                });
            }
        }
    }
    $: !inited && initDefaultState(items);

    function onOutro(item: AnimationItem | ChangeBoundsItem): void {
        animationList = animationList.filter(it => it !== item);

        if (item.resolvePromise) {
            item.resolvePromise();
        }
    }

    onDestroy(() => {
        if (prevStateId) {
            stateCtx.unregisterInstance(prevStateId);
        }
    });
</script>

{#if !hasError}
    <Outer
        cls={css.state}
        {componentContext}
        {layoutParams}
        parentOf={parentOfItems}
        parentOfSimpleMode={true}
        {replaceItems}
    >
        {#if selectedComponentContext}
            {#key selectedId}
                <Unknown
                    componentContext={selectedComponentContext}
                />
            {/key}
        {/if}

        <div class={css.state__animations} bind:this={animationRoot} aria-hidden="true">
            {#each animationList as item (item)}
                {#if 'direction' in item}
                    <div
                        class={css['state__animation-child']}
                        style:left="{item.offsetLeft}px"
                        style:top="{item.offsetTop}px"
                        style:width="{item.width}px"
                        style:height="{item.height}px"
                        in:inOutTransition|global={item}
                        on:introend={() => onOutro(item)}
                    >
                        <div class={css['state__animation-child-inner']}>
                            <Unknown
                                componentContext={item.componentContextCopy}
                            />
                        </div>
                    </div>
                {:else}
                    <div
                        class={css['state__animation-child']}
                        in:changeBoundsTransition|global={item}
                        on:introend={() => onOutro(item)}
                    >
                        <div class={css['state__animation-child-inner']}>
                            <Unknown
                                componentContext={item.componentContextCopy}
                            />
                        </div>
                    </div>
                {/if}
            {/each}
        </div>
    </Outer>

    {#if componentContext?.tooltips}
        {#each componentContext.tooltips as item (item.internalId)}
            <TooltipView
                ownerNode={item.ownerNode}
                data={item.desc}
                internalId={item.internalId}
                parentComponentContext={componentContext}
            />
        {/each}
    {/if}
{/if}
