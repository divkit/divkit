<script lang="ts">
    import {
        highlightElem,
        highlightMode,
        highlightModeClicked,
        selectedElem,
        selectedLeaf,
        webStructure,
        webStructureMap
    } from '../data/webStructure';
    import StructureCurrent from './StructureCurrent.svelte';
    import StructureTemplates from './StructureTemplates.svelte';
    import Tree from './Tree.svelte';
    import type { TreeLeaf } from '../ctx/tree';
    import { panelStructure } from '../data/panels';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    $: tree = $webStructure;
    let highlightLeaf: TreeLeaf | null = null;
    let lastHoveredWithKeyboard = false;
    let treeComponent: Tree;

    $: {
        if ($highlightElem) {
            highlightLeaf = webStructureMap?.get($highlightElem) || null;
        } else {
            highlightLeaf = null;
        }
    }

    $: {
        if ($selectedElem) {
            let leaf = webStructureMap?.get($selectedElem) || null;
            selectedLeaf.set(leaf || null);
            if ($highlightModeClicked && treeComponent) {
                highlightModeClicked.set(false);
                treeComponent.focus();
            }
        } else {
            selectedLeaf.set(null);
        }
    }

    function toggleSelect(): void {
        $highlightMode = !$highlightMode;
    }

    function getText(leaf: TreeLeaf): string {
        return leaf.props.json.type;
    }

    function onTreeHover(event: CustomEvent<TreeLeaf | null>): void {
        lastHoveredWithKeyboard = false;
        highlightElem.set(event.detail?.props.node || null);
    }

    function onTreeKeyboardHover(event: CustomEvent<TreeLeaf>): void {
        lastHoveredWithKeyboard = true;
        highlightElem.set(event.detail.props.node);
    }

    function onTreeSelect(event: CustomEvent<TreeLeaf | null>): void {
        selectedLeaf.set(event.detail);
    }

    function onWindowMouseMove(): void {
        if (lastHoveredWithKeyboard) {
            highlightElem.set(null);
        }
    }
</script>

<div class="structure">
    <div class="structure__title">
        {$l10n('structure')}

        <button
            class="structure__select"
            class:structure__select_toggled={$highlightMode}
            title={$l10n('selectComponent')}
            on:click={toggleSelect}
        ></button>

        <button class="structure__close" title="Close structure" on:click={() => panelStructure.set(false)}></button>
    </div>

    {#if tree}
        <Tree
            root={tree}
            {getText}
            selectedLeaf={$selectedLeaf}
            {highlightLeaf}
            on:selectionchange={onTreeSelect}
            on:hover={onTreeHover}
            on:keyboardhover={onTreeKeyboardHover}
            bind:this={treeComponent}
        />
    {:else}
        <div class="structure__empty">
            {$l10n('noStructure')}
        </div>
    {/if}

    <StructureCurrent />
    <StructureTemplates />
</div>

<svelte:window on:mousemove={onWindowMouseMove} />

<style>
    .structure {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        min-height: 0;
        overflow: hidden;
    }

    .structure__title {
        position: relative;
        display: flex;
        align-items: center;
        flex: 0 0 auto;
        height: 50px;
        padding: 0 12px;
        background: var(--bg-secondary);
    }

    .structure__select {
        width: 32px;
        height: 32px;
        margin: 0 0 0 16px;
        padding: 0;
        background: no-repeat 50% 50% url(../assets/selectElement.svg);
        background-color: var(--accent2);
        appearance: none;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: .1s ease-in-out;
        transition-property: background-color, opacity;
    }

    .structure__select:hover {
        opacity: .7;
    }

    .structure__select_toggled {
        background-color: var(--accent1);
    }

    .structure__empty {
        flex: 1 1 auto;
        padding: 16px 12px;
    }

    .structure__close {
        position: absolute;
        top: 0;
        bottom: 0;
        right: 12px;
        width: 32px;
        height: 32px;
        margin: auto 0;
        padding: 0;
        background: no-repeat 50% 50% url(../assets/close.svg);
        appearance: none;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        opacity: .7;
        transition: opacity .1s ease-in-out;
    }

    .structure__close:hover {
        opacity: 1;
    }
</style>
