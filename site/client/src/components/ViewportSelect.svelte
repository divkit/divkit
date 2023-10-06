<script lang="ts">
    import Select from './Select.svelte';

    // const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    $: variants = [{
        width: 320,
        height: 568
    }, {
        width: 360,
        height: 640,
        default: 1
    }, {
        width: 375,
        height: 667
    }, {
        width: 393,
        height: 851
    }, {
        width: 414,
        height: 896
    }/*, {
        value: 'custom',
        name: $l10n('customViewport')
    }*/].map(it => {
        if ('width' in it) {
            return {
                default: 'default' in it ? it.default : undefined,
                value: `${it.width}x${it.height}`,
                name: `${it.width}x${it.height}`
            };
        } else {
            return it;
        }
    });

    export let value = '';
    let selectVal = '';
    // let customWidth = '200';
    // let customHeight = '200';

    $: {
        if (!value) {
            const def = variants.find(it => 'default' in it && it.default);
            if (!def) {
                throw Error('No default variant');
            }
            value = selectVal = def.value;
            /* const parts = value.split('x');
            if (parts.length === 2) {
                customWidth = parts[0];
                customHeight = parts[1];
            } */
        }
    }

    function onChange(): void {
        if (selectVal !== 'custom') {
            /* const parts = selectVal.split('x');
            if (parts.length === 2) {
                customWidth = parts[0];
                customHeight = parts[1];
            } */
            value = selectVal;
        }
    }

    /*function onCustomChange(): void {
        if (selectVal === 'custom' && Number(customWidth) >= 200 && Number(customHeight) >= 200) {
            value = `${customWidth}x${customHeight}`;
        }
    }*/
</script>

<div class="viewport-select">
    <Select bind:value={selectVal} items={variants} toggledClass="viewport-select__toggled-select" on:change={onChange}>
        <div class="viewport-select__select">
            {selectVal}
            <svg xmlns="http://www.w3.org/2000/svg" class="viewport-select__select-icon" viewBox="0 0 23 23"><path fill="currentColor" d="M3.96 6.648c-.434 0-1.033.717-.499 1.439l7.663 7.888c.33.371.81.396 1.15.025l6.934-8.103c.275-.318.149-1.265-.575-1.28z"/></svg>
        </div>
    </Select>

    <!--{#if selectVal === 'custom'}
        <input
            class="viewport-select__custom"
            type="number"
            bind:value={customWidth}
            min="200"
            max="2000"
            step="1"
            on:input={onCustomChange}
        />
        <input
            class="viewport-select__custom"
            type="number"
            bind:value={customHeight}
            min="200"
            max="2000"
            step="1"
            on:input={onCustomChange}
        />
    {/if}-->
</div>

<style>
    .viewport-select {
        display: inline-block;
        vertical-align: top;
    }

    .viewport-select__select {
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

    .viewport-select__select-icon {
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

    .viewport-select__select:hover,
    :global(.viewport-select__toggled-select) .viewport-select__select {
        background-color: var(--accent0);
        color: var(--accent0-text);
    }

    .viewport-select__select:hover .viewport-select__select-icon,
    :global(.viewport-select__toggled-select) .viewport-select__select-icon {
        color: var(--accent0-text);
    }
</style>
