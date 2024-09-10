<script lang="ts">
    import { createEventDispatcher, type ComponentType, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';

    export let value: object;
    export let itemView: ComponentType;
    export let isMoving = false;
    export let isReoderInProgress = false;
    export let canBeMoved = true;
    export let readOnly = false;

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    let elem: HTMLElement;
</script>

<div
    class="move-item"
    class:move-item_readonly={readOnly}
    class:move-item_moving={isMoving}
    class:move-item_reorder={isReoderInProgress}
    bind:this={elem}
>
    {#if canBeMoved}
        <div
            class="move-item__move"
            on:pointerdown|preventDefault={event => dispatch('movestart', { event, elem })}
            on:pointerup|preventDefault
        ></div>
    {/if}

    <div class="move-item__content">
        <svelte:component
            this={itemView}
            bind:value={value}
            on:change
        />
    </div>

    {#if !readOnly}
        <button
            class="move-item__delete"
            aria-label={$l10nString('delete')}
            data-custom-tooltip={$l10nString('delete')}
            on:click|stopPropagation|preventDefault={() => dispatch('delete')}
        ></button>
    {/if}
</div>

<style>
    .move-item {
        position: relative;
        padding: 4px 48px 4px 20px;
        border-radius: 10px;
        transition: background-color .15s ease-in-out;
    }

    .move-item_readonly {
        padding-right: 4px;
    }

    .move-item:not(.move-item_readonly):hover {
        background-color: var(--fill-transparent-1);
    }

    .move-item_moving {
        transition-duration: 0s;
        background-color: var(--fill-transparent-1);
    }

    .move-item__move {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        box-sizing: border-box;
        width: 20px;
        opacity: 0;
        cursor: grab;
        background: no-repeat 50% 50% url(../../../assets/reorder.svg);
        filter: var(--icon-filter);
        transition: opacity .15s ease-in-out;
    }

    .move-item:not(.move-item_readonly):hover .move-item__move {
        opacity: 1;
    }

    .move-item_moving.move-item_moving.move-item_moving .move-item__move {
        opacity: .7;
        transition-duration: 0s;
        cursor: grabbing;
    }

    .move-item__delete {
        position: absolute;
        top: 8px;
        right: 8px;
        bottom: 8px;
        margin: 0;
        padding: 0;

        width: 32px;
        cursor: pointer;
        background: none;
        border: 1px solid transparent;
        border-radius: 6px;
        appearance: none;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color, opacity;
    }

    .move-item_reorder .move-item__delete {
        opacity: 0;
    }

    .move-item__delete::before {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% url(../../../assets/minus.svg);
        background-size: 20px;;
        filter: var(--icon-filter);
        content: '';
    }

    .move-item__delete:hover {
        background-color: var(--fill-transparent-1);
    }

    .move-item__delete:active {
        background-color: var(--fill-transparent-2);
    }

    .move-item__delete:focus-visible {
        outline: none;
        border-color: var(--accent-purple);
    }
</style>
