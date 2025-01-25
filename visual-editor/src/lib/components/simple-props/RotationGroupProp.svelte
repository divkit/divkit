<script lang="ts">
    import type { RotationProperty } from '../../data/componentProps';
    import NumberProp from './NumberProp.svelte';
    import RotationProp from './RotationProp.svelte';

    export let id: string = '';
    export let value: string | number;
    export let item: RotationProperty;
</script>

<div class="rotation-group-prop">
    <RotationProp
        {id}
        {item}
        value={Number(value)}
        on:change
        mix="rotation-group-prop__rotation"
    />

    <NumberProp
        {id}
        {item}
        {value}
        subtype="angle"
        min={0}
        max={360}
        on:change
        mix="rotation-group-prop__input"
    />
</div>

<style>
    .rotation-group-prop {
        display: flex;
        gap: 18px;
    }

    :global(.rotation-group-prop__rotation) {
        flex: 0 0 auto;
    }

    :global(.rotation-group-prop__input) {
        flex: 1 0 0;
    }
</style>
