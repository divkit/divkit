<script lang="ts">
    export let disabled = false;
    export let cls = '';
    export let title = '';
    export let slim = false;
    export let tag: 'button' | 'div' = 'button';
    export let theme: 'border' | 'border-gray' | 'normal' = 'border';
    export let size: 'small' | 'medium' = 'medium';
    export let centerContent = false;
    export let customTooltips = false;
</script>

<!-- svelte-ignore a11y_no_static_element_interactions -->
<svelte:element
    this={tag}
    class="button2 button2_theme_{theme} button2_size_{size} {cls}"
    class:button2_slim={slim}
    class:button2_center-content={centerContent}
    disabled={disabled ? true : undefined}
    data-custom-tooltip={customTooltips ? title : ''}
    title={customTooltips ? undefined : title}
    aria-label={title}
    on:click
>
    <slot />
</svelte:element>

<style>
    .button2 {
        position: relative;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 9px 15px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        border: 1px solid var(--fill-transparent-3);
        border-radius: 8px;
        background: var(--background-primary);
        appearance: none;
        transition: .15s ease-in-out;
        transition-property: border-color, background-color;
        cursor: pointer;
    }

    .button2_theme_border-gray {
        background: var(--fill-transparent-minus-1);
    }

    .button2_center-content {
        justify-content: center;
    }

    .button2_size_small {
        padding-top: 5px;
        padding-bottom: 5px;
    }

    .button2_theme_normal {
        background: var(--fill-transparent-2);
        border-color: transparent;
    }

    .button2_slim {
        width: 32px;
        height: 32px;
        padding: 1px 5px;
        border-radius: 6px;
    }

    .button2:not([disabled]):hover {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-2);
    }

    .button2_theme_normal:not([disabled]):hover {
        border-color: transparent;
        background-color: var(--fill-transparent-3);
    }

    .button2:not([disabled]):active {
        border-color: var(--fill-transparent-3);
        background-color: var(--fill-transparent-3);
    }

    .button2_theme_normal:not([disabled]):active {
        border-color: transparent;
        background-color: var(--fill-transparent-4);
    }

    .button2:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }

    .button2[disabled] {
        cursor: default;
        border: none;
        background: var(--fill-transparent-1);
    }

    .button2[disabled]::before {
        opacity: .33;
    }
</style>
