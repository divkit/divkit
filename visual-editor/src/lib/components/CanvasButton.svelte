<script lang="ts">
    export let node: HTMLElement | undefined = undefined;
    export let title: string;
    export let disabled = false;
</script>

<button
    class="canvas-button"
    {disabled}
    aria-label={title}
    data-custom-tooltip={title}
    bind:this={node}
    on:click
>
    <slot />
</button>

<style>
    .canvas-button {
        position: relative;
        display: flex;
        align-items: center;
        flex: 0 0 auto;
        min-width: 32px;
        height: 32px;
        margin: 0;
        padding: 0;
        font: inherit;
        border: none;
        border-radius: 8px;
        background-color: var(--fill-transparent-2);
        appearance: none;
        transition: background-color .15s ease-in-out;
        cursor: pointer;
    }

    .canvas-button[disabled] {
        cursor: default;
    }

    .canvas-button:focus-visible {
        outline: 2px solid var(--accent-purple);
    }

    .canvas-button:not([disabled]):hover {
        background-color: var(--fill-transparent-3);
    }

    .canvas-button:not([disabled]):active {
        background-color: var(--fill-transparent-4);
    }
</style>
