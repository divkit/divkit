<script lang="ts">
    import type { ComponentProperty } from '../../data/componentProps';
    import SimplePropsList from './SimplePropsList.svelte';
    import Spoiler2 from '../controls/Spoiler2.svelte';
    import type { SelectedElemProps } from '../../ctx/appContext';

    export let title: string;
    export let propsList: ComponentProperty[];
    export let processedJson: Record<string, unknown>;
    export let parentProcessedJson: Record<string, unknown> | null;
    export let parentEvalJson: Record<string, unknown> | null;
    export let evalJson: Record<string, unknown> | null;
    export let selectedElemProps: SelectedElemProps | null;
</script>

<Spoiler2 theme="props">
    <div slot="title">{title}</div>

    <SimplePropsList
        {propsList}
        {processedJson}
        {parentProcessedJson}
        {parentEvalJson}
        {evalJson}
        {selectedElemProps}
        on:change
    />
</Spoiler2>
