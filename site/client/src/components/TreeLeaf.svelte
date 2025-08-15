<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { TreeContext, TreeLeaf } from '../ctx/tree';
    import { TREE_CTX } from '../ctx/tree';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    export let leaf: TreeLeaf;
    export let level = 0;

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    const ctx = getContext<TreeContext>(TREE_CTX);
    const collapsedStore = ctx.collapsedStore;
    const selectedStore = ctx.selectedStore;
    const highlightStore = ctx.highlightStore;
    const getText = ctx.getText;
    let node: HTMLElement;

    $: alternateHover = $highlightStore === leaf;

    let selected = false;
    $: {
        const wasSelected = selected;
        selected = $selectedStore === leaf;
        if (selected && !wasSelected && node) {
            dispatch('selected', { node });
        }
    }

    $: expanded = !$collapsedStore[leaf.id];

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
        dispatch('hovered', { leaf });
    }

    function onOut(): void {
        dispatch('hovered', { leaf: null });
    }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<div
    class="tree-leaf"
    class:tree-leaf_alternate-hovered={alternateHover}
    class:tree-leaf_selected={selected}
    class:tree-leaf_empty_no={Boolean(leaf.childs.length)}
    class:tree-leaf_empty_yes={!leaf.childs.length}
    class:tree-leaf_expanded_no={!expanded}
    class:tree-leaf_expanded_yes={expanded}
    style="--level: {level}"
    on:click={onClick}
    on:mouseenter={onOver}
    on:mouseleave={onOut}
    bind:this={node}
>
    <div
        class="tree-leaf__icon"
        on:click={onIconClick}
        title={leaf.childs.length ? (expanded ? $l10n('collapse') : $l10n('expand')) : undefined}
    ></div>
    {getText(leaf)}
</div>
{#if expanded}
    {#each leaf.childs as child (child.id)}
        <svelte:self leaf={child} level={level + 1} on:selected on:hovered />
    {/each}
{/if}

<style>
    .tree-leaf {
        position: relative;
        padding: 6px 12px;
        padding-left: calc(36px + var(--level) * 24px);
        cursor: pointer;
        user-select: none;
        word-break: break-word;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        border: 1px solid transparent;
        border-radius: 1024px;
    }

    .tree-leaf__icon {
        position: absolute;
        top: 0;
        bottom: 0;
        left: calc(4px + var(--level) * 24px);
        width: 24px;
        background: no-repeat 50% 50%;
        opacity: .1;
        transition: opacity .1s ease-in-out;
    }

    .tree-leaf_empty_yes .tree-leaf__icon {
        background-image: url(../assets/dot.svg);
    }

    .tree-leaf_empty_no .tree-leaf__icon {
        opacity: .3;
    }

    .tree-leaf_empty_no.tree-leaf_expanded_no .tree-leaf__icon {
        background-image: url(../assets/arrowRight.svg);
    }

    .tree-leaf_empty_no.tree-leaf_expanded_yes .tree-leaf__icon {
        background-image: url(../assets/arrowDown.svg);
    }

    .tree-leaf:hover,
    .tree-leaf_alternate-hovered {
        border-color: var(--accent1-semi);
    }

    .tree-leaf_selected,
    .tree-leaf_selected:hover {
        border-color: var(--accent1);
    }

    .tree-leaf_empty_no .tree-leaf__icon:hover {
        opacity: .7;
    }
</style>
