<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { TextAlignProperty } from '../../data/componentProps';
    import RadioSelector from '../controls/RadioSelector.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import iconLeft from '../../../assets/alignLeft.svg?url';
    import iconLeftLogical from '../../../assets/alignLeftLogical.svg?url';
    import iconCenter from '../../../assets/alignCenter.svg?url';
    import iconRight from '../../../assets/alignRight.svg?url';
    import iconRightLogical from '../../../assets/alignRightLogical.svg?url';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: string;
    export let item: TextAlignProperty;

    const dispatch = createEventDispatcher();
    const { state, directionSelector } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
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
    options={directionSelector ? [{
        text: $l10n('props.align_start'),
        value: 'start',
        icon: iconLeftLogical
    }, {
        text: $l10n('props.align_center'),
        value: 'center',
        icon: iconCenter
    }, {
        text: $l10n('props.align_end'),
        value: 'end',
        icon: iconRightLogical
    }] : [{
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
