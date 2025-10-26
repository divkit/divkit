<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { ComponentProperty } from '../../data/componentProps';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import Select from '../Select.svelte';
    import type { ItemTabs } from '../../utils/items';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';

    export let value: number;
    export let item: ComponentProperty;
    export let processedJson: unknown | undefined;

    const { state } = getContext<AppContext>(APP_CTX);
    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange(event: CustomEvent<string>): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value: event.detail ? Number(event.detail) : undefined
            });
        }
    }

    $: items = [{
        text: $l10nString('emptyValue'),
        value: '',
        isEmpty: true
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
    }, ...(processedJson as any)?.items.map((it: ItemTabs, index: number) => {
        return {
            text: it.title,
            value: String(index)
        };
    })];
</script>

<Select
    mix="tabs-selected-tab-prop"
    disabled={$readOnly}
    value={typeof value === 'number' ? String(value) : ''}
    on:change={onChange}
    theme="normal"
    size="medium"
    items={items}
/>
