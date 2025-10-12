<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { APP_CTX, type AppContext, type MenuItem } from '../ctx/appContext';
    import { encodeBackground } from '../utils/encodeBackground';

    export let item: MenuItem;
    export let selected = false;

    const { contextMenu } = getContext<AppContext>(APP_CTX);

    let elem: HTMLElement;

    const dispatch = createEventDispatcher();
</script>

<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
<!-- svelte-ignore a11y_click_events_have_key_events -->
<li
    bind:this={elem}
    class="menu-item"
    class:menu-item_icon={item.icon}
    class:menu-item_enabled={item.enabled !== false}
    class:menu-item_selected={selected}
    on:click|stopPropagation|preventDefault={() => {
        if (item.enabled === false) {
            return;
        }
        if (item.callback?.() !== false) {
            contextMenu().hide();
        }
    }}
    on:mouseenter={item.submenu ? () => {
        dispatch('showsubmenu', { elem });
    } : null}
    on:mouseleave={item.submenu ? () => {
        dispatch('hidesubmenu');
    } : null}
>
    {#if item.icon}
        <span
            class="menu-item__icon"
            style:background-image="url({encodeBackground(item.icon)})"
        ></span>
    {/if}

    {item.text}

    {#if item.submenu}
        <span class="menu-item__submenu-icon"></span>
    {/if}
</li>

<style>
    .menu-item {
        position: relative;
        display: flex;
        align-items: center;
        padding: 8px 16px;
        font-size: 14px;
        line-height: 20px;
        /* transition: background-color .15s ease-in-out; */
        color: var(--text-tertiary);
    }

    .menu-item_icon {
        padding-left: 12px;
    }

    .menu-item_enabled {
        color: var(--text-primary);
        cursor: pointer;
    }

    .menu-item_enabled:hover,
    .menu-item_selected {
        background: var(--fill-transparent-1);
    }

    .menu-item_enabled:not(.menu-item_selected):active {
        background: var(--fill-transparent-2);
    }

    .menu-item__icon {
        width: 20px;
        height: 20px;
        margin-right: 8px;
        background: 50% 50% no-repeat;
        background-size: contain;
        filter: var(--icon-filter);
    }

    .menu-item__submenu-icon {
        width: 12px;
        height: 12px;
        margin-left: auto;
        background: no-repeat 50% 50% url(../../assets/submenu.svg);
        background-size: contain;
        filter: var(--icon-filter);
    }
</style>
