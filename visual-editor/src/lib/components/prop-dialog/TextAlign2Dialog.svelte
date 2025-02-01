<script lang="ts">
    import { getContext } from 'svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import RadioSelector from '../controls/RadioSelector.svelte';
    import iconLeft from '../../../assets/alignLeft.svg?url';
    import iconLeftLogical from '../../../assets/alignLeftLogical.svg?url';
    import iconCenter from '../../../assets/alignCenter.svg?url';
    import iconRight from '../../../assets/alignRight.svg?url';
    import iconRightLogical from '../../../assets/alignRightLogical.svg?url';
    import { APP_CTX, type AppContext, type TextAlign2DialogShowProps } from '../../ctx/appContext';
    import { Truthy } from '../../utils/truthy';

    const { directionSelector, state } = getContext<AppContext>(APP_CTX);
    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    const { direction } = state;

    $: if (isShown && !disabled) {
        callback(value);
    }

    export function show(props: TextAlign2DialogShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        disabled = props.disabled;
        isShown = true;
    }

    export function hide(): void {
        isShown = false;
    }

    let target: HTMLElement;
    let isShown = false;
    let value: string;
    let callback: (val: string) => void;
    let disabled: boolean | undefined;

    $: iconsCount = directionSelector ? 5 : 3;

    function onClose(): void {
        isShown = false;
    }

    function onChange(): void {
        callback(value);
    }
</script>

{#if isShown && target}
    <ContextDialog
        direction="down"
        offsetY={12}
        {target}
        width="{10 + iconsCount * 32 + (iconsCount - 1) * 4}px"
        on:close={onClose}
    >
        <RadioSelector
            name="text_alignment_horizontal"
            theme="tertiary"
            defaultValue="left"
            {disabled}
            options={[{
                text: $l10n('props.align_left'),
                value: 'left',
                icon: iconLeft
            }, directionSelector && {
                text: $direction === 'ltr' ? $l10n('props.align_start') : $l10n('props.align_end'),
                value: $direction === 'ltr' ? 'start' : 'end',
                icon: iconLeftLogical
            }, {
                text: $l10n('props.align_center'),
                value: 'center',
                icon: iconCenter
            }, directionSelector && {
                text: $direction === 'ltr' ? $l10n('props.align_end') : $l10n('props.align_start'),
                value: $direction === 'ltr' ? 'end' : 'start',
                icon: iconRightLogical
            }, {
                text: $l10n('props.align_right'),
                value: 'right',
                icon: iconRight
            }].filter(Truthy)}
            customTooltips={false}
            bind:value={value}
            on:change={onChange}
        />
    </ContextDialog>
{/if}
