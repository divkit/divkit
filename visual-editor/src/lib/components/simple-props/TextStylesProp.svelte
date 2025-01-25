<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { TextStylesProperty } from '../../data/componentProps';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import CheckboxGroup from '../controls/CheckboxGroup.svelte';
    import iconUnderline from '../../../assets/underline2.svg?url';
    import iconStrike from '../../../assets/strike2.svg?url';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let processedJson;
    export let item: TextStylesProperty;

    const dispatch = createEventDispatcher();
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    function onChange(event: CustomEvent<{
        name: string;
        value: boolean;
    }>) {
        dispatch('change', {
            item,
            values: [{
                prop: event.detail.name,
                value: event.detail.value ? 'single' : undefined
            }]
        });
    }
</script>

<CheckboxGroup
    on:change={onChange}
    disabled={$readOnly}
    list={[{
        text: $l10n('props.underline'),
        name: 'underline',
        value: processedJson.underline === 'single',
        icon: iconUnderline
    }, {
        text: $l10n('props.strike'),
        name: 'strike',
        value: processedJson.strike === 'single',
        icon: iconStrike
    }]}
/>
