<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import type { SelectOptionsShowProps } from '../../ctx/appContext';
    import type { SelectOption } from '../../utils/select';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    $: if (isShown && !readOnly && callback) {
        callback(value);
    }

    export function show(props: SelectOptionsShowProps): void {
        callback = props.callback;
        target = props.target;
        value = props.value;
        readOnly = props.readOnly;
        isShown = true;
    }

    export function hide(): void {
        isShown = false;
    }

    let target: HTMLElement;
    let isShown = false;
    let value: SelectOption;
    let callback: ((val: SelectOption) => void) | undefined;
    let readOnly: boolean | undefined;

    function onClose(): void {
        isShown = false;
    }
</script>

{#if isShown && target}
    <ContextDialog {target} on:close={onClose} canMove={true}>
        <div class="select-options-dialog__content">
            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="select-options-dialog__label">
                        {$l10n('select.text')}
                    </div>
                    <Text
                        bind:value={value.text}
                        disabled={readOnly}
                    />
                </label>
            </div>

            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="select-options-dialog__label">
                        {$l10n('select.value')}
                    </div>
                    <Text
                        bind:value={value.value}
                        disabled={readOnly}
                        required
                    />
                </label>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .select-options-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 24px;
        margin: 16px;
    }

    .select-options-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }
</style>
