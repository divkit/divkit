<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import Select from '../Select.svelte';

    export let value: string;
    export let item: ComponentProperty;

    const { lang } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state, customFontFaces } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange(): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value
            });
        }
    }
</script>

<Select
    mix="selct-font-family-prop"
    disabled={$readOnly}
    bind:value={value}
    on:change={onChange}
    theme="normal"
    size="medium"
    items={[{
        value: '',
        text: ''
    }, ...customFontFaces.map(it => ({
        value: it.value,
        text: it.text[$lang]
    }))]}
/>
