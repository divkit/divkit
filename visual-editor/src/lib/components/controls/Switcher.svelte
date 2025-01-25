<script lang="ts">
    export let checked: boolean;
    export let disabled: boolean = false;
</script>

<div
    class="switcher"
    class:switcher_disabled={disabled}
>
    <input class="switcher__input"
        type="checkbox"
        {disabled}
        bind:checked={checked}
        on:change
    >
    <div class="switcher__bg"></div>
    <div class="switcher__tumbler"></div>
</div>

<style>
    .switcher {
        position: relative;
        width: 36px;
        height: 20px;
        border-radius: 1024px;
    }

    .switcher__input {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        border: none;
        border-radius: inherit;
        appearance: none;
        cursor: pointer;
    }

    .switcher_disabled .switcher__input {
        cursor: default;;
    }

    .switcher__input:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .switcher__bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: inherit;
        background: var(--fill-transparent-3);
        transition: background-color .15s ease-in-out;
        pointer-events: none;
    }

    .switcher:not(.switcher_disabled) .switcher__input:hover ~ .switcher__bg {
        background-color: var(--fill-transparent-4);
    }

    .switcher:not(.switcher_disabled) .switcher__input:checked ~ .switcher__bg {
        background-color: var(--accent-purple);
    }

    .switcher_disabled .switcher__input:checked ~ .switcher__bg {
        background-color: var(--fill-accent-3);
    }

    .switcher__tumbler {
        position: absolute;
        top: 2px;
        left: 2px;
        width: 16px;
        height: 16px;
        background: #fff;
        border-radius: 1024px;
        pointer-events: none;
        transition: transform .15s ease-in-out;
    }

    .switcher__input:checked ~ .switcher__tumbler {
        transform: translateX(16px);
    }
</style>
