<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { TextAlignProperty } from '../../data/componentProps';
    import RadioSelector from '../controls/RadioSelector.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import iconLeft from '../../../assets/alignLeft.svg?url';
    import iconCenter from '../../../assets/alignCenter.svg?url';
    import iconRight from '../../../assets/alignRight.svg?url';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: string;
    export let item: TextAlignProperty;

    const dispatch = createEventDispatcher();
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    function onChange() {
        dispatch('change', {
            value,
            item
        });
    }
</script>

<RadioSelector
    name={item.prop}
    theme="tertiary"
    defaultValue="left"
    disabled={$readOnly}
    options={[{
        text: $l10n('props.align_left'),
        value: 'left',
        icon: iconLeft
    }, {
        text: $l10n('props.align_center'),
        value: 'center',
        icon: iconCenter
    }, {
        text: $l10n('props.align_right'),
        value: 'right',
        icon: iconRight
    }]}
    bind:value={value}
    on:change={onChange}
/>
