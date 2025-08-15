<script lang="ts">
    import { getContext } from 'svelte';
    import ComponentsTree from './ComponentsTree.svelte';
    import type { TreeLeaf } from '../ctx/tree';
    import { isTemplate, namedTemplates } from '../data/templates';
    import PanelTitle from './PanelTitle.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import { supportedComponents } from '../data/componentProps';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    const { l10n, lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { highlightLeaf, highlightElem, highlightRanges, tree } = state;

    let highlightLeafs: TreeLeaf[] | null = null;
    let treeComponent: ComponentsTree;

    $: {
        if ($highlightLeaf) {
            highlightLeafs = $highlightLeaf?.filter(Boolean) || null;
        } else {
            highlightLeafs = null;
        }
    }

    function getText(leaf: TreeLeaf): string {
        if (leaf === $tree) {
            return $l10n('rootComponent');
        }

        const type = leaf.props.json.type;

        if (isTemplate(type)) {
            return $l10n(namedTemplates[type].nameKey);
        }

        if (supportedComponents.has(type)) {
            return $l10n(`components.${type}`);
        }

        return type;
    }

    function onTreeHover(event: CustomEvent<TreeLeaf | null>): void {
        const leaf = event.detail;
        const node = leaf?.props.node;
        const range = leaf?.props.range;
        if (node && range) {
            highlightLeaf.set([leaf]);
            highlightElem.set([node]);
            highlightRanges.set([range]);
        } else {
            highlightLeaf.set(null);
            highlightElem.set(null);
            highlightRanges.set(null);
        }
    }

    function onTreeKeyboardHover(event: CustomEvent<TreeLeaf>): void {
        highlightLeaf.set([event.detail]);
        highlightElem.set([event.detail.props.node]);
        const range = event.detail.props.range;
        highlightRanges.set(range ? [event.detail.props.range] : null);
    }
</script>

<PanelTitle title={$l10n('components')} />

{#if $tree}
    {#key $lang}
        <ComponentsTree
            {highlightLeafs}
            {getText}
            on:hover={onTreeHover}
            on:keyboardhover={onTreeKeyboardHover}
            bind:this={treeComponent}
        />
    {/key}
{/if}
