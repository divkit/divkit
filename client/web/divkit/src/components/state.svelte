<script lang="ts">
    import { setContext, getContext, tick } from 'svelte';

    import css from './state.module.css';

    import type { LayoutParams } from '../types/layoutParams';
    import type { DivBase, TemplateContext } from '../../typings/common';
    import type { DivStateData, State } from '../types/state';
    import type { AnyTransition, AppearanceTransition, DivBaseData, TransitionChange } from '../types/base';
    import type { ChangeBoundsTransition } from '../types/base';
    import { ROOT_CTX, RootCtxValue } from '../context/root';
    import { wrapError } from '../utils/wrapError';
    import { STATE_CTX, StateCtxValue, StateInterface } from '../context/state';
    import { calcMaxDuration, inOutTransition } from '../utils/inOutTransition';
    import { changeBoundsTransition } from '../utils/changeBoundsTransition';
    import { flattenTransition } from '../utils/flattenTransition';
    import Outer from './outer.svelte';
    import Unknown from './unknown.svelte';

    export let json: Partial<DivStateData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);
    let animationRoot: HTMLElement | undefined;
    let transitionChangeBoxes: Map<string, DOMRect> = new Map();

    let hasError = false;
    $: items = json.states || [];
    $: {
        if (!items?.length) {
            hasError = true;
            rootCtx.logError(wrapError(new Error('Empty "states" prop for div "state"')));
        } else {
            hasError = false;
        }
    }

    let childStateMap: Map<string, StateInterface> | null = null;

    interface AnimationItem {
        json: DivBaseData;
        templateContext: TemplateContext;
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
        templateContext: TemplateContext;
        rootBbox: DOMRect;
        beforeBbox: DOMRect;
        afterBbox: DOMRect;
        transition: ChangeBoundsTransition;
        resolvePromise?: (val?: void) => void;
        node: HTMLElement;
    }
    let animationList: (AnimationItemWithMaxDuration | ChangeBoundsItem)[] = [];

    interface ChildWithTransition {
        json: DivBaseData;
        templateContext: TemplateContext;
        transitions: AppearanceTransition;
        node: HTMLElement;
        resolvePromise?: (val?: void) => void;
    }
    interface ChildWithTransitionChange {
        id: string;
        json: DivBaseData;
        templateContext: TemplateContext;
        transitions: TransitionChange;
        node: HTMLElement;
        resolvePromise?: (val?: void) => void;
    }
    let childrenWithTransitionIn: ChildWithTransition[] = [];
    let childrenWithTransitionOut: ChildWithTransition[] = [];
    let childrenWithTransitionChange: ChildWithTransitionChange[] = [];

    function getItemAnimation(rootBbox: DOMRect, child: ChildWithTransition, direction: 'in' | 'out'): AnimationItem {
        let { json, templateContext, transitions, node } = child;
        json = rootCtx.getJsonWithVars(json) as DivBaseData;
        transitions = rootCtx.getJsonWithVars(transitions) as AppearanceTransition;

        const transitionsList: AnyTransition[] = flattenTransition(transitions);
        const bbox = node.getBoundingClientRect();

        return {
            json: {
                ...json,
                transition_in: undefined,
                transition_out: undefined,
                transition_change: undefined,
                margins: undefined,
                alpha: undefined
            },
            templateContext,
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

    const stateId = json.div_id || json.id;

    if (!stateId) {
        hasError = true;
        rootCtx.logError(wrapError(new Error('Missing "id" prop for div "state"')));
    } else {
        const stateCtx = getContext<StateCtxValue>(STATE_CTX);
        stateCtx.registerInstance(stateId, {
            async setState(stateId: string) {
                if (selectedId === stateId) {
                    return;
                }

                rootCtx.setRunning('stateChange', true);

                animationList.forEach(it => {
                    if (it.resolvePromise) {
                        it.resolvePromise();
                    }
                });
                animationList = [];
                let transitionsOutToRun: AnimationItem[] = [];
                if (animationRoot) {
                    const rootBbox = animationRoot.getBoundingClientRect();
                    transitionsOutToRun = childrenWithTransitionOut.map(it => getItemAnimation(rootBbox, it, 'out'));
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
                    selectedState = newState;
                } else {
                    rootCtx.logError(wrapError(new Error('Cannot find state with id'), {
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
                    childrenWithTransitionIn.map(it => getItemAnimation(rootBbox, it, 'in'));

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
                        const res: ChangeBoundsItem = {
                            json: {
                                ...child.json,
                                transition_in: undefined,
                                transition_out: undefined,
                                transition_change: undefined,
                                margins: undefined,
                                alpha: undefined,
                                width: { type: 'match_parent' },
                                height: { type: 'match_parent' },
                            },
                            templateContext: child.templateContext,
                            rootBbox,
                            beforeBbox: transitionChangeBoxes.get(child.id) as DOMRect,
                            afterBbox: child.node.getBoundingClientRect(),
                            node: child.node,
                            transition: getTransitionChange(child.transitions) as ChangeBoundsTransition,
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
            },
            getChild(id: string): StateInterface | undefined {
                if (childStateMap && childStateMap.has(id)) {
                    return childStateMap.get(id);
                }

                rootCtx.logError(wrapError(new Error('Missing state block with id'), {
                    additional: {
                        id
                    }
                }));
                return undefined;
            }
        });

        setContext<StateCtxValue>(STATE_CTX, {
            registerInstance(id: string, block: StateInterface) {
                if (!childStateMap) {
                    childStateMap = new Map();
                }

                if (childStateMap.has(id)) {
                    rootCtx.logError(wrapError(new Error('Duplicate state with id'), {
                        additional: {
                            id
                        }
                    }));
                } else {
                    childStateMap.set(id, block);
                }
            },
            runVisibilityTransition(
                json: DivBaseData,
                templateContext: TemplateContext,
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
                        templateContext,
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
                templateContext: TemplateContext,
                transitions: AppearanceTransition,
                node: HTMLElement
            ) {
                const item: ChildWithTransition = {
                    json,
                    templateContext,
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
                templateContext: TemplateContext,
                transitions: AppearanceTransition,
                node: HTMLElement
            ) {
                const item: ChildWithTransition = {
                    json,
                    templateContext,
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
                templateContext: TemplateContext,
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
                    templateContext,
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
            }
        });
    }

    let selectedId: string | undefined;
    let selectedState: State | null = null;
    const jsonDefaultStateId = rootCtx.getJsonWithVars(json.default_state_id);
    let inited = false;
    function initDefaultState(items: State[]): void {
        if (inited) {
            return;
        }
        inited = true;

        if (items.length) {
            if (jsonDefaultStateId) {
                selectedId = jsonDefaultStateId;
                selectedState = items.find(it => it.state_id === selectedId) || null;
                if (!selectedState) {
                    rootCtx.logError(wrapError(new Error('Cannot find state for default_state_id'), {
                        additional: {
                            selectedId
                        }
                    }));
                }
            } else {
                selectedState = items[0];
                selectedId = selectedState.state_id;
            }
        }
    }
    $: initDefaultState(items);

    function onOutro(item: AnimationItem | ChangeBoundsItem): void {
        animationList = animationList.filter(it => it !== item);

        if (item.resolvePromise) {
            item.resolvePromise();
        }
    }
</script>

{#if !hasError}
    <Outer
        cls={css.state}
        {json}
        {origJson}
        {templateContext}
        {layoutParams}
    >
        {#if selectedState?.div}
            {#key selectedState}
                <Unknown div={selectedState.div} templateContext={templateContext} />
            {/key}
        {/if}
        <div class={css.state__animations} bind:this={animationRoot}>
            {#each animationList as item (item)}
                {#if 'direction' in item}
                    <div
                        class={css['state__animation-child']}
                        style="left:{item.offsetLeft}px;top:{item.offsetTop}px;width:{item.width}px;height:{item.height}px"
                        in:inOutTransition={item}
                        on:introend={() => onOutro(item)}
                    >
                        <div class={css['state__animation-child-inner']}>
                            <Unknown div={item.json} templateContext={item.templateContext} />
                        </div>
                    </div>
                {:else}
                    <div
                        class={css['state__animation-child']}
                        in:changeBoundsTransition={item}
                        on:introend={() => onOutro(item)}
                    >
                        <div class={css['state__animation-child-inner']}>
                            <Unknown div={item.json} templateContext={item.templateContext} />
                        </div>
                    </div>
                {/if}
            {/each}
        </div>
    </Outer>
{/if}
