<script context="module">
    let showVar;

    export function show(elem, msg) {
        if (showVar) {
            showVar(elem, msg);
        }
    }
</script>

<script>
    import {fly} from 'svelte/transition';

    let popup;
    let visible = false;
    let elem;
    let msg;
    let coords = {};
    let showTs = 0;

    function calcCoords(elem) {
        const bbox = elem.getBoundingClientRect();

        return {
            top: bbox.bottom + 10,
            left: bbox.left + bbox.width / 2 - 150
        };
    }

    showVar = (elem2, msg2) => {
        coords = calcCoords(elem2);
        elem = elem2;
        msg = msg2;
        visible = true;
        showTs = Date.now();
    };

    function onWindowClick(event) {
        if (Date.now() - showTs > 1000 && event.target && popup && !popup.contains(event.target)) {
            visible = false;
        }
    }

    function onCloseClick() {
        visible = false;
    }

    function onResize() {
        if (elem && visible) {
            coords = calcCoords(elem);
        }
    }
</script>

{#if visible}
    <div
        bind:this={popup}
        class="pointing-popup"
        style="left:{coords.left}px;top:{coords.top}px" transition:fly={{y: 40}}
    >
        <div class="pointing-popup__tail"></div>
        <div class="pointing-popup__content">
            {msg}
        </div>
        <div class="pointing-popup__close" on:click={onCloseClick}></div>
    </div>
{/if}

<svelte:window
    on:click={onWindowClick}
    on:resize={onResize}
/>

<style>
    .pointing-popup {
        position: absolute;
        box-sizing: border-box;
        width: 300px;
        padding: 20px 25px 20px 25px;
        background: var(--alt-bg);
        border-radius: 20px;
        color: var(--alt-text);
        word-break: break-word;
    }

    .pointing-popup__content :global(a) {
        color: inherit;
        text-decoration: underline;
    }

    .pointing-popup__content :global(a):hover {
        text-decoration: none;
    }

    .pointing-popup__tail {
        position: absolute;
        top: -10px;
        left: 50%;
        margin-left: -11px;
        border: 11px solid transparent;
        border-top-width: 0;
        border-bottom-color: var(--alt-bg);
    }

    .pointing-popup__close {
        position: absolute;
        top: 0;
        right: 0;
        width: 60px;
        height: 60px;
        background: no-repeat 50% 50% url(../assets/closeWhite.svg);
        opacity: .6;
        appearance: none;
        cursor: pointer;
        border: none;
    }

    .pointing-popup__close:hover {
        opacity: 1;
    }
</style>
