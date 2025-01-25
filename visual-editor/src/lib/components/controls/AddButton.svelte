<script lang="ts">
    import Button2 from './Button2.svelte';

    export let disabled = false;
    export let cls = '';
    export let title = '';
    export let slim = false;
</script>

<Button2
    {disabled}
    {cls}
    {title}
    {slim}
    on:click
>
    <div
        class="add-button__icon"
        class:add-button__icon_disabled={disabled}
    />
    <slot />
</Button2>

<style>
    .add-button__icon {
        display: block;
        flex: 0 0 auto;
        width: 20px;
        height: 20px;
        background: no-repeat 50% 50% url(../../../assets/add.svg);
        background-size: 20px;
        filter: var(--icon-filter);
        content: '';
    }

    .add-button__icon_disabled {
        opacity: .3;
    }
</style>
