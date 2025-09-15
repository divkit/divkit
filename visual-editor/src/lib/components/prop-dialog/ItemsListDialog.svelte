<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import Text from '../controls/Text.svelte';
    import ContextDialog from './ContextDialog.svelte';
    import { APP_CTX, type AppContext, type ItemsListShowProps } from '../../ctx/appContext';
    import type { Item } from '../../utils/items';
    import Select from '../Select.svelte';
    import { fullComponentsList } from '../../data/components';

    const { state } = getContext<AppContext>(APP_CTX);
    const { l10n, l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);

    const {
        userDefinedTemplates
    } = state;

    $: if (isShown && !readOnly && callback) {
        if (isInitial) {
            isInitial = false;
        } else {
            callback(value);
        }
    }

    export function show(props: ItemsListShowProps): void {
        isInitial = true;
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
    let value: Item;
    let callback: ((val: Item) => void) | undefined;
    let readOnly: boolean | undefined;
    let isInitial = false;

    $: items = (fullComponentsList.concat($userDefinedTemplates.map(type => ({
        name: type,
        type: type,
        isTemplate: true
    })))).map(it => {
        return {
            text: it.name || it.nameKey && $l10nString(it.nameKey) || '<unknown>',
            value: it.type,
            icon: state.componentIcon(it.type)
        };
    });

    function onClose(): void {
        isShown = false;
    }

    function onIdChange(event: CustomEvent<{
        value: string;
    }>): void {
        value = {
            state_id: event.detail.value,
            div: value.div
        };
    }

    function onTypeChange(event: CustomEvent<string>): void {
        value = {
            state_id: value.state_id,
            div: {
                type: event.detail
            }
        };
    }
</script>

{#if isShown && target}
    <ContextDialog
        {target}
        on:close={onClose}
        canMove={true}
        overflow="visible"
    >
        <div class="items-list-dialog__content">
            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="items-list-dialog__label">
                        {$l10n('state.id')}
                    </div>
                    <Text
                        value={value.state_id}
                        disabled={readOnly}
                        on:change={onIdChange}
                    />
                </label>
            </div>

            <div>
                <!-- svelte-ignore a11y-label-has-associated-control -->
                <label>
                    <div class="items-list-dialog__label">
                        {$l10n('state.div')}
                    </div>
                    <Select
                        theme="normal"
                        iconTheme="transparent"
                        size="medium"
                        value={value.div.type}
                        items={items}
                        on:change={onTypeChange}
                    />
                </label>
            </div>
        </div>
    </ContextDialog>
{/if}

<style>
    .items-list-dialog__content {
        display: flex;
        flex-direction: column;
        gap: 24px;
        margin: 16px;
    }

    .items-list-dialog__label {
        margin-bottom: 6px;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-secondary);
    }
</style>
