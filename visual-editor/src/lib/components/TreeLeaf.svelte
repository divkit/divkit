<script lang="ts">
    import { createEventDispatcher, getContext, onDestroy, onMount } from 'svelte';
    import { slide } from 'svelte/transition';
    import type { TreeContext, TreeLeaf } from '../ctx/tree';
    import { TREE_CTX } from '../ctx/tree';
    import type { LanguageContext } from '../ctx/languageContext';
    import { LANGUAGE_CTX } from '../ctx/languageContext';
    import { isSimpleElement } from '../data/schema';
    import { fullComponentsList } from '../data/components';
    import { isViewerWarning } from '../data/rendererErrors';
    import { isUserTemplateWithoutChilds } from '../data/userTemplates';
    import { namedTemplates } from '../data/templates';
    import { Truthy } from '../utils/truthy';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    export let leaf: TreeLeaf;
    export let level = 0;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, showErrors, contextMenu } = getContext<AppContext>(APP_CTX);
    const { userDefinedTemplates, copiedLeaf, rendererErrors, readOnly, themeStore } = state;

    const dispatch = createEventDispatcher();

    const ctx = getContext<TreeContext>(TREE_CTX);
    const collapsedStore = ctx.collapsedStore;
    const selectedStore = ctx.selectedStore;
    const highlightStore = ctx.highlightStore;
    const dragTarget = ctx.dragTarget;
    const bindChild = ctx.bindChild;
    const getText = ctx.getText;

    let node: HTMLElement;

    $: items = (fullComponentsList.concat($userDefinedTemplates.map(type => ({
        name: type,
        type: type,
        isTemplate: true
    })))).map(it => {
        return {
            name: it.name || it.nameKey && $l10nString(it.nameKey) || '<unknown>',
            type: it.type,
            description: it.description
        };
    });

    $: baseType = state.getBaseType(leaf.props.json.type);

    $: canHaveChilds = baseType &&
        !isSimpleElement(baseType) &&
        !isUserTemplateWithoutChilds(state, leaf.props.json) &&
        !namedTemplates[leaf.props.json.type];

    $: menuItems = [canHaveChilds && {
        text: $l10nString('addComponent'),
        submenu: items.map(it => {
            return {
                text: it.name,
                icon: state.componentIcon(it.type),
                callback() {
                    dispatch('add', {
                        leaf,
                        node,
                        type: it.type
                    });
                }
            };
        }),
    }, {
        text: $l10nString('cloneComponent'),
        enabled: Boolean(leaf.parent),
        callback() {
            dispatch('action', { action: 'duplicate', leaf, node });
        }
    }, {
        text: $l10nString('copyComponent'),
        enabled: Boolean(leaf.parent),
        callback() {
            dispatch('action', { action: 'copy', leaf, node });
        }
    }, {
        text: $l10nString('pasteComponent'),
        enabled: Boolean($copiedLeaf),
        callback() {
            dispatch('action', { action: 'paste', leaf, node });
        }
    }, {
        text: $l10nString('removeComponent'),
        enabled: Boolean(leaf.parent),
        callback() {
            dispatch('action', { action: 'delete', leaf, node });
        }
    }].filter(Truthy);

    $: alternateHover = $highlightStore && $highlightStore.includes(leaf);

    let selected = false;
    let wasSelected = false;
    $: {
        selected = $selectedStore?.id === leaf?.id;
        if (selected && !wasSelected && node) {
            dispatch('selected', { leaf, node });
            wasSelected = true;
        } else {
            wasSelected = false;
        }
    }

    $: expanded = !$collapsedStore[leaf.id];

    let errorType: 'errors' | 'warnings' | '' = '';
    $: {
        const errorsList = $rendererErrors[leaf.id];
        if (errorsList?.every(isViewerWarning)) {
            errorType = 'warnings';
        } else if (errorsList) {
            errorType = 'errors';
        } else {
            errorType = '';
        }
    }

    function onClick(): void {
        selectedStore.set(leaf);
    }

    function onIconClick(): void {
        collapsedStore.set({
            ...$collapsedStore,
            [leaf.id]: expanded
        });
    }

    function onOver(): void {
        dispatch('hovered', { leaf, node });
    }

    function onOut(): void {
        dispatch('hovered', { leaf: null });
    }

    function onDragStart(event: DragEvent): void {
        if ($readOnly) {
            event.preventDefault();
            return;
        }

        if (event.dataTransfer) {
            event.dataTransfer.setData('application/divnode', leaf.id);
            // todo return D&D image
            /* const img = getDragImage(leaf.props.json.type);
            if (img) {
                event.dataTransfer.setDragImage(img, 0, 0);
            } */
            event.dataTransfer.dropEffect = 'move';
        }
    }

    function showLeafErrors(): void {
        showErrors({
            leafId: leaf.id
        });
    }

    onMount(() => {
        bindChild({
            type: 'mount',
            node,
            leaf() {
                return leaf;
            }
        });
    });

    onDestroy(() => {
        bindChild({
            type: 'destroy',
            node
        });
    });
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div
    class="tree-leaf"
    class:tree-leaf_alternate-hovered={alternateHover}
    class:tree-leaf_selected={selected}
    class:tree-leaf_empty_no={Boolean(leaf.childs.length)}
    class:tree-leaf_empty_yes={!leaf.childs.length}
    class:tree-leaf_expanded_no={!expanded}
    class:tree-leaf_expanded_yes={expanded}
    class:tree-leaf_can-have-childs={canHaveChilds}
    class:tree-leaf_container-child={!isSimpleElement(leaf.parent?.props.json.type)}
    class:tree-leaf_errors={errorType === 'errors'}
    class:tree-leaf_warnings={errorType === 'warnings'}
    data-level={level}
    style="--level: {level}"
    draggable="true"
    on:dragstart={onDragStart}
    on:click={onClick}
    on:mouseenter={onOver}
    on:mouseleave={onOut}
    bind:this={node}
>
    {#if $dragTarget?.node === node && $dragTarget?.type === 'before'}
        <div class="tree-leaf__drag-target tree-leaf__drag-target_before"></div>
    {/if}

    {#if canHaveChilds}
        {@const title = expanded ? $l10nString('collapse') : $l10nString('expand')}
        {#key expanded}
            <div
                class="tree-leaf__icon"
                on:click|stopPropagation={onIconClick}
                aria-label={title}
                data-custom-tooltip={title}
            ></div>
        {/key}
    {/if}
    <div class="tree-leaf__type-icon-outer">
        <div
            class="tree-leaf__type-icon"
            style:background-image="url({state.componentIcon(leaf.props.json.type, leaf.props.json)})"
        ></div>
    </div>
    {getText(leaf)}
    <div class="tree-leaf__menu-spacer"></div>
    {#if errorType}
        <div
            class="tree-leaf__error-icon"
            class:tree-leaf__error-icon_dark={$themeStore === 'dark'}
            class:tree-leaf__error-icon_warn={errorType === 'warnings'}
            aria-label={$l10nString(`treeItem.${errorType}`)}
            data-custom-tooltip={$l10nString(`treeItem.${errorType}`)}
            on:click={showLeafErrors}
        ></div>
    {/if}
    {#if !$readOnly}
        <div class="tree-leaf__menu-outer">
            <div
                class="tree-leaf__menu"
                aria-label={$l10nString('treeMenu')}
                data-custom-tooltip={$l10nString('treeMenu')}
                on:click={event => contextMenu().toggle({
                    name: 'tree',
                    owner: event.target,
                    items: menuItems,
                    offset: {
                        x: -8,
                        y: 0
                    }
                })}
            ></div>
        </div>
    {/if}
</div>

{#if $dragTarget?.node === node && $dragTarget?.type === 'inside'}
    <div class="tree-leaf__drag-target tree-leaf__drag-target_inside" style="--level: {level + 1}"></div>
{/if}

{#if expanded}
    <div class="tree-leaf__children" transition:slide|local>
        {#each leaf.childs as child (child.id)}
            <svelte:self
                leaf={child}
                level={level + 1}
                on:selected
                on:hovered
                on:action
                on:add
            />
        {/each}
    </div>
{/if}

{#if $dragTarget?.node === node && $dragTarget?.type === 'after'}
    <div class="tree-leaf__drag-target tree-leaf__drag-target_after" style="--level: {level}"></div>
{/if}

<style>
    .tree-leaf {
        position: relative;
        padding: 6px 40px 6px 12px;
        padding-left: calc(42px + var(--level) * 24px + 24px);
        display: flex;
        align-items: center;
        min-width: max-content;
        font-size: 14px;
        line-height: 20px;
        cursor: pointer;
        user-select: none;
        word-break: break-word;
        white-space: nowrap;
        text-overflow: ellipsis;
        border: 1px solid transparent;
        transition: background-color 0s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf_can-have-childs {
        padding-left: calc(36px + var(--level) * 24px + 24px + 6px);
    }

    .tree-leaf__icon {
        position: absolute;
        top: 0;
        bottom: 0;
        left: calc(4px + var(--level) * 24px);
        flex: 0 0 auto;
        width: 24px;
        background: no-repeat 50% 50%;
        opacity: .1;
        transition: opacity .1s ease-in-out;
        filter: var(--icon-filter);
    }

    .tree-leaf_empty_yes .tree-leaf__icon {
        background-image: url(../../assets/dot.svg);
    }

    .tree-leaf_empty_no .tree-leaf__icon {
        opacity: .3;
    }

    .tree-leaf_empty_no.tree-leaf_expanded_no .tree-leaf__icon {
        background-image: url(../../assets/arrowRight.svg);
    }

    .tree-leaf_empty_no.tree-leaf_expanded_yes .tree-leaf__icon {
        background-image: url(../../assets/arrowDown.svg);
    }

    .tree-leaf__type-icon-outer {
        position: absolute;
        top: 0;
        bottom: 0;
        left: calc(4px + (var(--level) + 1) * 24px + 6px);
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        margin: auto;
        border-radius: 4px;
        background: var(--fill-transparent-2);
    }

    .tree-leaf__type-icon {
        position: absolute;
        top: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50%;
        background-size: contain;
        filter: var(--icon-filter);
    }

    .tree-leaf__error-icon {
        width: 20px;
        height: 20px;
        margin-right: 8px;
        background: no-repeat 50% 50% url(../../assets/errorsLight.svg);
        background-size: 16px;
        opacity: .5;
        cursor: pointer;
        transition: opacity .15s ease-in-out;
    }

    .tree-leaf__error-icon_dark {
        background-image: url(../../assets/errorsLight.svg);
    }

    .tree-leaf__error-icon_warn {
        background-image: url(../../assets/warnings.svg);
    }

    .tree-leaf__error-icon:hover {
        opacity: .7;
    }

    .tree-leaf__error-icon:active {
        opacity: .5;
    }

    .tree-leaf:not(.tree-leaf_selected):hover,
    .tree-leaf_alternate-hovered {
        background-color: var(--fill-transparent-1);
        transition: background-color 0s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf:not(.tree-leaf_selected):active {
        background-color: var(--fill-transparent-2);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf_errors {
        background-color: var(--fill-red-1);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf:not(.tree-leaf_selected).tree-leaf_errors:not(.tree-leaf_selected):hover,
    .tree-leaf_alternate-hovered.tree-leaf_errors:not(.tree-leaf_selected) {
        background-color: var(--fill-red-2);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf_warnings {
        background-color: var(--fill-orange-1);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf:not(.tree-leaf_selected).tree-leaf_warnings:not(.tree-leaf_selected):hover,
    .tree-leaf_alternate-hovered.tree-leaf_warnings:not(.tree-leaf_selected) {
        background-color: var(--fill-orange-2);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf_selected {
        cursor: default;
        background-color: var(--fill-accent-2);
        transition: background-color .15s ease-in-out, color .15s  ease-in-out;
    }

    .tree-leaf_empty_no .tree-leaf__icon:hover {
        opacity: .7;
    }

    .tree-leaf_empty_no .tree-leaf__icon:active {
        opacity: .5;
    }

    .tree-leaf__menu-spacer {
        flex: 1 0 auto;
    }

    .tree-leaf__menu-outer {
        position: absolute;
        top: 0;
        right: 12px;
        bottom: 0;
        width: 32px;
        height: 32px;
        margin: auto 0;
    }

    .tree-leaf__menu {
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../assets/menu.svg);
        background-size: 20px;
        opacity: 0;
        transition: opacity 0s ease-in-out;
        cursor: pointer;
        filter: var(--icon-filter);
    }

    .tree-leaf:hover .tree-leaf__menu,
    .tree-leaf_errors .tree-leaf__menu,
    .tree-leaf_warnings .tree-leaf__menu {
        opacity: .4;
    }

    .tree-leaf_selected .tree-leaf__menu.tree-leaf__menu {
        opacity: .4;
    }

    .tree-leaf:hover .tree-leaf__menu:hover {
        opacity: .8;
        transition-duration: .15s;
    }

    .tree-leaf:hover .tree-leaf__menu:active {
        opacity: .6;
        transition-duration: .15s;
    }

    .tree-leaf__drag-target {
        position: absolute;
        left: calc(36px + var(--level) * 24px);
        right: 0;
        height: 4px;
        margin-top: -2px;
        border-radius: 1024px;
        background: var(--accent-purple);
        pointer-events: none;
    }

    .tree-leaf__drag-target_before {
        top: -1px;
    }

    .tree-leaf__children {
        will-change: transform;
        overflow: clip;
    }
</style>
