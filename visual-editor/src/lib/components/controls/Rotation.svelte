<script lang="ts">
    import { createEventDispatcher } from 'svelte';

    export let id: string = '';
    export let value: number;
    export let mix = '';
    export let disabled = false;
    export let inverse = false;

    const dispatch = createEventDispatcher();

    let input: HTMLInputElement;
    let setting = false;
    let timeout: number | undefined;

    function onPointerMove(event: PointerEvent): void {
        event.preventDefault();

        const bbox = input.getBoundingClientRect();
        const dx = event.clientX - bbox.left - bbox.width / 2;
        const dy = event.clientY - bbox.top - bbox.height / 2;
        const angle = Math.atan2(dy, dx);

        let val = inverse ?
            - Math.round(angle / Math.PI * 180) :
            Math.round(angle / Math.PI * 180);

        if (!inverse) {
            val += 90;
        }
        if (val < 0) {
            val += 360;
        }
        setting = true;
        value = val;
        input.value = String(val);
        dispatch('change', {
            value,
        });

        clearTimeout(timeout);
        timeout = setTimeout(() => {
            setting = false;
        }, 100);
    }

    function onInput(): void {
        if (!setting) {
            value = Number(input.value);
            dispatch('change', {
                value,
            });
        }
    }

    function onPointerUp(): void {
        document.body.removeEventListener('pointermove', onPointerMove);
        document.body.removeEventListener('pointerup', onPointerUp);
        document.body.removeEventListener('pointercancel', onPointerUp);
    }

    function onPointerDown(event: PointerEvent): void {
        if (disabled) {
            return;
        }

        onPointerMove(event);
        input.focus();

        document.body.addEventListener('pointermove', onPointerMove);
        document.body.addEventListener('pointerup', onPointerUp);
        document.body.addEventListener('pointercancel', onPointerUp);
    }
</script>

<div
    class="rotation {mix}"
    class:rotation_disabled={disabled}
>
    <input
        type="range"
        min="0"
        max="360"
        step="1"
        class="rotation__input"
        autocomplete="off"
        {id}
        bind:this={input}
        {value}
        {disabled}
        on:input={onInput}
        on:pointerdown={onPointerDown}
        on:focus
        on:blur
    />

    <div class="rotation__value" style:--value={(inverse ? (0 - (value % 360) / 360) : (value % 360) / 360 - .25)}></div>
</div>

<style>
    .rotation {
        position: relative;
        box-sizing: border-box;
        width: 40px;
        height: 40px;
        border: 1px solid var(--fill-transparent-3);
        background: var(--fill-transparent-minus-1);
        border-radius: 100%;
        transition: border-color .15s ease-in-out;
    }

    .rotation_disabled {
        border: none;
        background: var(--fill-transparent-1);
    }

    .rotation:not(.rotation_disabled):hover {
        border-color: var(--fill-transparent-4);
    }

    .rotation:focus-within {
        border-color: var(--accent-purple);
    }

    .rotation:focus-within:hover {
        border-color: var(--accent-purple-hover);
    }

    .rotation__input {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: 100%;
        height: 100%;
        appearance: none;
        opacity: 0;
    }

    .rotation__input:focus-visible {
        outline: none;
    }

    .rotation__value {
        position: absolute;
        top: calc(50% - 1.5px);
        left: calc(50% - 1.5px);
        width: 15px;
        height: 3px;
        border-radius: 1024px;
        background: currentColor;
        transform: rotate(calc(360deg * var(--value)));
        transform-origin: 1.5px 1.5px;
        pointer-events: none;
    }
</style>
