<script lang="ts">
    export let value: string;
    export let enabled: boolean;
</script>

<div class="color-input" class:color-input_disabled={!enabled}>
    <div class="color-input__preview" style:background={value}>
        <input
            type="color"
            class="color-input__color-input"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            disabled={!enabled}
            bind:value={value}
        />
    </div>

    <input
        type="text"
        autocomplete="off"
        autocorrect="off"
        autocapitalize="off"
        spellcheck="false"
        class="color-input__input"
        disabled={!enabled}
        bind:value={value}
    >
</div>

<style>
    .color-input {
        display: flex;
        gap: 8px;
    }

    .color-input__preview {
        position: relative;
        box-sizing: border-box;
        width: 28px;
        height: 28px;
        border: 6px solid var(--bg-secondary);
        border-radius: 4px;
        transition: background-color .15s ease-in-out;
    }

    .color-input:not(.color-input_disabled) .color-input__preview:hover {
        background-color: var(--bg-tertiary);
    }

    .color-input__color-input {
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        border: none;
        opacity: 0;
    }

    .color-input:not(.color-input_disabled) .color-input__color-input {
        cursor: pointer;
    }

    .color-input__input {
        flex: 1 1 auto;
        margin: 0;
        padding: 4px 8px;
        font: inherit;
        color: inherit;
        background: var(--bg-secondary);
        border-radius: 4px;
        border: none;
    }
</style>
