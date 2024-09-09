<script lang="ts">
    import { getContext } from 'svelte';
    import PropsGroup from './props/PropsGroup.svelte';
    import { getSchemaByType, getSchemaNameByType } from '../data/schema';
    import SimpleProps from './simple-props/SimpleProps.svelte';
    import { getTemplateBaseType, isTemplate } from '../data/templates';
    import PanelTitle from './PanelTitle.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import { Truthy } from '../utils/truthy';
    import { APP_CTX, type AppContext } from '../ctx/appContext';

    export let showComplex: boolean = false;

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { selectedLeaf } = state;

    $: selectedType = $selectedLeaf?.props.processedJson?.type || null;
    $: baseType = state.getBaseType(selectedType);

    let props: string[] | null;

    $: {
        props = null;

        if (baseType) {
            const template = isTemplate(baseType);
            const templateBaseType = template ? getTemplateBaseType(baseType) : undefined;
            // eslint-disable-next-line no-nested-ternary
            const desc = template ?
                templateBaseType ? getSchemaByType(templateBaseType) : undefined :
                getSchemaByType(baseType);
            const allOf = desc?.allOf;
            if (allOf) {
                props = allOf.slice().reverse().map(it => {
                    if (it.$ref) {
                        return it.$ref.replace(/^div-/, '').replace(/\.json$/, '');
                    }

                    const template = isTemplate(baseType);
                    const templateBaseType = template ? getTemplateBaseType(baseType) : undefined;

                    // eslint-disable-next-line no-nested-ternary
                    return isTemplate(baseType) ?
                        templateBaseType ? getSchemaNameByType(templateBaseType) : undefined :
                        getSchemaNameByType(baseType);
                }).filter(Truthy);
            }
        }
    }
</script>

<div class="props">
    {#if showComplex}
        <PanelTitle title={$l10n(showComplex ? 'complexComponentProperties' : 'componentProperties')} />
    {/if}

    {#if props}
        <!-- to disable the animation on component change -->
        {#key $selectedLeaf?.id}
            {#if showComplex}
                {#each props as group}
                    <PropsGroup {group} />
                {/each}
            {:else}
                <SimpleProps />
            {/if}
        {/key}
    {:else}
        <div class="props__empty">
            <div class="props__empty-gap props__empty-gap_start"></div>
            {$l10n('chooseComponent')}
            <div class="props__empty-gap props__empty-gap_end"></div>
        </div>
    {/if}
</div>

<style>
    .props {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        overflow: auto;
    }

    .props__empty {
        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        align-items: center;
        font-size: 13px;
        color: var(--text-secondary);
    }

    .props__empty-gap_start {
        flex: 1 0 0;
    }

    .props__empty-gap_end {
        flex: 3 0 0;
    }
</style>
