<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import { evalCondition, type ConditionObject } from '../../data/props';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import Select from '../Select.svelte';
    import { Truthy } from '../../utils/truthy';

    export let value: string;
    export let item: ComponentProperty;
    export let processedJson: unknown | undefined = undefined;
    export let parentProcessedJson: unknown | undefined = undefined;
    export let hasEmpty = true;
    export let required = false;
    export let options: {
        rawName?: string;
        name?: string;
        value: string;
        show?: ConditionObject;
        icon?: string;
    }[];

    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    $: filteredOptions = options.filter(
        it => it.show === undefined ||
            processedJson && parentProcessedJson &&
            evalCondition(processedJson, parentProcessedJson, it.show)
    );

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    const dispatch = createEventDispatcher();

    function onChange() {
        dispatch('change', {
            value,
            item
        });
    }
</script>

<Select
    mix="selct-prop"
    disabled={$readOnly}
    bind:value={value}
    on:change={onChange}
    theme="normal"
    size="medium"
    {required}
    items={[hasEmpty && {
        value: '',
        text: ''
    }, ...filteredOptions.map(it => ({
        value: it.value,
        text: it.rawName || it.name && $l10nString(it.name) || '',
        icon: it.icon
    }))].filter(Truthy)}
/>
