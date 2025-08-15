<script lang="ts">
    import { slide } from 'svelte/transition';
    import { selectedLeaf } from '../data/webStructure';
    import JsonView from './JsonView.svelte';
    import StructureBox from './StructureBox.svelte';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    let expanded = false;

    $: currentJson = $selectedLeaf?.props.json;

    function toggle(): void {
        expanded = !expanded;
    }
</script>

<div class="structure-current">
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="structure-current__title" on:click={toggle}>
        {$l10n('currentComponent')}
    </div>
    {#if expanded}
        <div class="structure-current__content" transition:slide>
            {#if currentJson}
                {#if $selectedLeaf}
                    <StructureBox node={$selectedLeaf.props.node} />
                    <div class="structure-current__splitter"></div>
                {/if}
                <JsonView json={currentJson} expanded={true} />
            {:else}
                {$l10n('noStructure')}
            {/if}
        </div>
    {/if}
</div>

<style>
    .structure-current {
        display: flex;
        flex-direction: column;
        flex: 0 1 auto;
        min-height: 43px;
        max-height: 300px;
        background: var(--bg-secondary);
    }

    .structure-current__title {
        flex: 0 0 auto;
        padding: 12px 12px;
        cursor: pointer;
        transition: background .1s ease-in-out;
        user-select: none;
    }

    .structure-current__title:hover {
        text-decoration: underline;
    }

    .structure-current__content {
        overflow: auto;
        padding: 8px 12px;
        background: var(--bg-primary);
    }

    .structure-current__splitter {
        margin: 20px 0;
        border-top: 1px solid;
        opacity: .4;
    }
</style>
