<script lang="ts">
    import { createEventDispatcher, setContext } from 'svelte';
    import { writable } from 'svelte/store';
    import TreeLeafView from './TreeLeaf.svelte';
    import { treeDown, treeLeft, treeRight, treeUp } from '../utils/shortcuts';
    import type { TreeContext, TreeGetText, TreeLeaf } from '../ctx/tree';
    import { TREE_CTX } from '../ctx/tree';

    export let root: TreeLeaf;
    export let showRoot = true;
    export let getText: TreeGetText;
    export let selectedLeaf: TreeLeaf | null = null;
    export let highlightLeaf: TreeLeaf | null = null;

    export function focus(): void {
        if (rootNode) {
            rootNode.focus();
        }
    }

    let rootNode: HTMLElement;

    let prevSelectedLeaf: TreeLeaf | null = null;
    const collapsedStore = writable<Record<string, boolean>>({});
    const selectedStore = writable<TreeLeaf | null>(null);
    const highlightStore = writable<TreeLeaf | null>(null);

    const dispatch = createEventDispatcher();

    $: {
        $highlightStore = highlightLeaf;
    }

    $: {
        $selectedStore = prevSelectedLeaf = selectedLeaf;
    }

    $: linearList = linearize(root, $collapsedStore);

    function linearize(tree: TreeLeaf | null, collapsed: Record<string, boolean>): TreeLeaf[] {
        const res: TreeLeaf[] = [];

        function proc(leaf: TreeLeaf | null): void {
            if (!leaf) {
                return;
            }

            if (leaf !== tree || showRoot) {
                res.push(leaf);
            }

            if (!collapsed[leaf.id]) {
                leaf.childs.forEach(proc);
            }
        }

        proc(tree);

        return res;
    }

    function moveSelection(change: number): void {
        const selected = $selectedStore;
        let current = selected ? linearList.findIndex(it => it === selected) : -1;

        if (current === -1) {
            current = change === -1 ? 0 : linearList.length - 1;
        } else {
            current += change;
            if (current >= linearList.length) {
                current = linearList.length - 1;
            } else if (current < 0) {
                current = 0;
            }
        }

        const leaf = linearList[current];
        selectedStore.set(leaf);

        dispatch('keyboardhover', leaf);
    }

    function collapseOrMoveToParent(): void {
        const current = $selectedStore;

        if (!current) {
            return;
        }

        const collapsed = $collapsedStore;
        if (collapsed[current.id] || !current.childs.length) {
            if (current.parent) {
                selectedStore.set(current.parent);
            }
        } else {
            collapsedStore.set({
                ...collapsed,
                [current.id]: true
            });
        }
    }

    function expandOrMoveToChild(): void {
        const current = $selectedStore;

        if (!current) {
            return;
        }

        const collapsed = $collapsedStore;
        if (collapsed[current.id]) {
            collapsedStore.set({
                ...collapsed,
                [current.id]: false
            });
        } else {
            if (current.childs.length) {
                selectedStore.set(current.childs[0]);
            }
        }
    }

    function onKeydown(event: KeyboardEvent): void {
        if (event.ctrlKey || event.shiftKey || event.metaKey || event.altKey) {
            return;
        }

        if (treeUp.isPressed(event) || treeDown.isPressed(event)) {
            moveSelection(treeUp.isPressed(event) ? -1 : 1);
        } else if (treeLeft.isPressed(event)) {
            collapseOrMoveToParent();
        } else if (treeRight.isPressed(event)) {
            expandOrMoveToChild();
        } else {
            return;
        }

        event.preventDefault();
    }

    function onChildSelect(event: CustomEvent<{
        node: HTMLElement;
    }>): void {
        const node = event.detail.node;
        const bbox = node.getBoundingClientRect();
        const rootBbox = rootNode.getBoundingClientRect();

        if (bbox.top < rootBbox.top) {
            rootNode.scrollTop += bbox.top - rootBbox.top;
        } else if (bbox.bottom > rootBbox.bottom) {
            rootNode.scrollTop += bbox.bottom - rootBbox.bottom;
        }
    }

    function onChildHover(event: CustomEvent<{
        leaf: TreeLeaf;
    }>): void {
        dispatch('hover', event.detail.leaf);
    }

    selectedStore.subscribe(leaf => {
        if (leaf !== prevSelectedLeaf) {
            prevSelectedLeaf = leaf;
            dispatch('selectionchange', leaf);
        }
    });

    setContext<TreeContext>(TREE_CTX, {
        collapsedStore,
        selectedStore,
        highlightStore,
        getText
    });
</script>

<div class="tree" tabindex="0" on:keydown={onKeydown} bind:this={rootNode}>
    {#if showRoot}
        <TreeLeafView leaf={root} on:selected={onChildSelect} on:hovered={onChildHover} />
    {:else}
        {#each root.childs as leaf}
            <TreeLeafView {leaf} on:selected={onChildSelect} on:hovered={onChildHover} />
        {/each}
    {/if}
</div>

<style>
    .tree {
        height: 100%;
        overflow-y: auto;
    }
</style>
