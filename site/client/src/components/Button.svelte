<script lang="ts">
    export let theme: 'filled' | 'border' = 'border';
</script>

<button class="button" class:button_filled={theme === 'filled'} on:click>
    <slot />
</button>

<style>
    .button {
        margin: 0;
        padding: 6px 16px;
        font: inherit;
        font-size: 16px;
        line-height: inherit;
        background: none;
        border: 1px solid var(--accent0);
        color: var(--accent0);
        border-radius: 1024px;
        appearance: none;
        cursor: pointer;
        user-select: none;
        transition: .15s ease-in-out;
        transition-property: border-color, color, background-color;
    }

    .button:hover {
        background-color: var(--accent0);
        color: var(--accent0-text);
    }

    .button_filled {
        background-color: var(--accent0);
        color: var(--accent0-text);
    }

    .button_filled:hover {
        background-color: transparent;
        color: var(--accent0);
    }
</style>
