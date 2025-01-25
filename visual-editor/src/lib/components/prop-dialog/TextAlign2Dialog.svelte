<script lang="ts">
    import { getContext } from 'svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import RadioSelector from '../controls/RadioSelector.svelte';
    import iconLeft from '../../../assets/alignLeft.svg?url';
    import iconCenter from '../../../assets/alignCenter.svg?url';
    import iconRight from '../../../assets/alignRight.svg?url';
    import type { TextAlign2DialogShowProps } from '../../ctx/appContext';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

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
    let dialog;
    let isShown = false;
    let value: string;
    let callback: (val: string) => void;
    let disabled: boolean | undefined;

    function onClose(): void {
        isShown = false;
    }

    function onChange(): void {
        callback(value);
    }
</script>

{#if isShown && target}
    <ContextDialog bind:this={dialog} direction="down" offsetY={12} {target} width="114px" on:close={onClose}>
        <RadioSelector
            name="text_alignment_horizontal"
            theme="tertiary"
            defaultValue="left"
            {disabled}
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
            customTooltips={false}
            bind:value={value}
            on:change={onChange}
        />
    </ContextDialog>
{/if}
