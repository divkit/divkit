<script lang="ts">
    import Select from './Select.svelte';

    interface Variant {
        name: string;
        value: string;
        default?: number
    }
    const VARIANTS: Variant[] = [{
        name: 'LTR',
        value: 'ltr',
        default: 1
    }, {
        name: 'RTL',
        value: 'rtl'
    }];

    export let value = '';

    $: {
        if (!value) {
            const def = VARIANTS.find(it => 'default' in it && it.default);
            if (!def) {
                throw new TypeError('No default variant');
            }
            value = def.value;
        }
    }
</script>

<Select bind:value={value} items={VARIANTS} toggledClass="direction-select__toggled-select">
    <div class="direction-select__select">
        {value.toUpperCase()}
        <svg xmlns="http://www.w3.org/2000/svg" class="direction-select__select-icon" viewBox="0 0 23 23"><path fill="currentColor" d="M3.96 6.648c-.434 0-1.033.717-.499 1.439l7.663 7.888c.33.371.81.396 1.15.025l6.934-8.103c.275-.318.149-1.265-.575-1.28z"/></svg>
    </div>
</Select>

<style>
    .direction-select__select {
        position: relative;
        display: inline-flex;
        align-items: center;
        height: 32px;
        padding: 0 32px 0 12px;
        appearance: none;
        font: inherit;
        line-height: inherit;
        background: none;
        /*background: no-repeat 50% 50% #346e6d url(../assets/selectIcon.svg);*/
        background-position: calc(100% - 6px) 50%;
        transition: .1s ease-in-out;
        transition-property: color, background-color;
        border: 1px solid var(--accent0);
        border-radius: 1024px;
        color: var(--accent0);
        cursor: pointer;
        user-select: none;
    }

    .direction-select__select-icon {
        position: absolute;
        top: 0;
        right: 7px;
        bottom: 0;
        width: 18px;
        height: 18px;
        margin: auto;
        color: var(--accent0);
        transition: color .1s ease-in-out;
    }

    .direction-select__select:hover,
    :global(.direction-select__toggled-select) .direction-select__select {
        background-color: var(--accent0);
        color: var(--accent0-text);
    }

    .direction-select__select:hover .direction-select__select-icon,
    :global(.direction-select__toggled-select) .direction-select__select-icon {
        color: var(--accent0-text);
    }
</style>
